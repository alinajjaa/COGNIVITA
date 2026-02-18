import tensorflow as tf
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.layers import Dense, GlobalAveragePooling2D, Dropout
from tensorflow.keras.models import Model
from tensorflow.keras.callbacks import EarlyStopping
import matplotlib.pyplot as plt
from pathlib import Path
import numpy as np
from sklearn.utils.class_weight import compute_class_weight

# -------------------
# PARAMETERS
# -------------------
IMG_SIZE = 224
BATCH_SIZE = 32
EPOCHS = 15

# -------------------
# RESOLVE DATASET PATHS
# -------------------
script_dir = Path(__file__).resolve().parent
dataset_candidates = [
    script_dir / "dataset_3class",
    script_dir / "venv" / "dataset_3class",
]
dataset_dir = next((p for p in dataset_candidates if p.exists()), None)
if dataset_dir is None:
    raise FileNotFoundError(
        f"Dataset folder 'dataset_3class' not found. Checked: {', '.join(str(p) for p in dataset_candidates)}"
    )

train_dir = str(dataset_dir / "train")
val_dir = str(dataset_dir / "val")

# -------------------
# DATA GENERATORS
# -------------------
train_gen = ImageDataGenerator(
    rescale=1./255,
    rotation_range=15,
    zoom_range=0.1,
    horizontal_flip=True
)

val_gen = ImageDataGenerator(rescale=1./255)

train_data = train_gen.flow_from_directory(
    train_dir,
    target_size=(IMG_SIZE, IMG_SIZE),
    batch_size=BATCH_SIZE,
    class_mode="categorical"
)

val_data = val_gen.flow_from_directory(
    val_dir,
    target_size=(IMG_SIZE, IMG_SIZE),
    batch_size=BATCH_SIZE,
    class_mode="categorical"
)

print(f"Using dataset at: {dataset_dir}")
print(f"Classes: {train_data.class_indices}")
print(f"Train images: {train_data.samples} | Val images: {val_data.samples}")

# -------------------
# CLASS WEIGHTS (CRITICAL FIX)
# -------------------
class_labels = train_data.classes
class_weights = compute_class_weight(
    class_weight="balanced",
    classes=np.unique(class_labels),
    y=class_labels
)
class_weights = dict(enumerate(class_weights))
print("Class weights:", class_weights)

# -------------------
# MODEL
# -------------------
base_model = MobileNetV2(
    weights="imagenet",
    include_top=False,
    input_shape=(IMG_SIZE, IMG_SIZE, 3)
)

# 🔓 Fine-tuning: unfreeze top layers
for layer in base_model.layers[:-30]:
    layer.trainable = False

for layer in base_model.layers[-30:]:
    layer.trainable = True

x = base_model.output
x = GlobalAveragePooling2D()(x)
x = Dense(128, activation="relu")(x)
x = Dropout(0.4)(x)
output = Dense(3, activation="softmax")(x)

model = Model(inputs=base_model.input, outputs=output)

# -------------------
# COMPILE (LOW LR)
# -------------------
model.compile(
    optimizer=tf.keras.optimizers.Adam(learning_rate=1e-5),
    loss="categorical_crossentropy",
    metrics=["accuracy"]
)

model.summary()

# -------------------
# TRAIN
# -------------------
early_stop = EarlyStopping(
    monitor="val_loss",
    patience=4,
    restore_best_weights=True
)

history = model.fit(
    train_data,
    validation_data=val_data,
    epochs=EPOCHS,
    callbacks=[early_stop],
    class_weight=class_weights
)

# -------------------
# SAVE MODEL
# -------------------
model.save("alzheimer_cnn_3class.h5")

# -------------------
# PLOT ACCURACY
# -------------------
plt.plot(history.history["accuracy"], label="Train Accuracy")
plt.plot
