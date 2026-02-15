import tensorflow as tf
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.layers import Dense, GlobalAveragePooling2D, Dropout
from tensorflow.keras.models import Model
from tensorflow.keras.callbacks import EarlyStopping
import matplotlib.pyplot as plt
from pathlib import Path
import os

# -------------------
# PARAMETERS
# -------------------
IMG_SIZE = 224
BATCH_SIZE = 32
EPOCHS = 10

# -------------------
# DATA PATHS & GENERATORS
# -------------------
# Resolve dataset directory relative to this script. Prefer ./dataset, fallback to ./venv/dataset
script_dir = Path(__file__).resolve().parent
dataset_candidates = [
    script_dir / "dataset",
    script_dir / "venv" / "dataset",
]
dataset_dir = next((p for p in dataset_candidates if p.exists()), None)
if dataset_dir is None:
    raise FileNotFoundError(
        f"Dataset folder not found. Checked: {', '.join(str(p) for p in dataset_candidates)}"
    )

train_dir = str(dataset_dir / "train")
val_dir = str(dataset_dir / "val")

def _count_images(root: Path) -> int:
    if not root.exists():
        return 0
    exts = {".jpg", ".jpeg", ".png", ".bmp"}
    count = 0
    for sub in root.rglob("*"):
        if sub.is_file() and sub.suffix.lower() in exts:
            count += 1
    return count

train_count = _count_images(Path(train_dir))
val_count = _count_images(Path(val_dir))

use_split = val_count == 0

if use_split:
    print("No validation images found; using 20% of train as validation.")
    train_gen = ImageDataGenerator(
        rescale=1./255,
        rotation_range=15,
        zoom_range=0.1,
        horizontal_flip=True,
        validation_split=0.2,
    )

    train_data = train_gen.flow_from_directory(
        train_dir,
        target_size=(IMG_SIZE, IMG_SIZE),
        batch_size=BATCH_SIZE,
        class_mode="categorical",
        subset="training",
        seed=42,
    )

    val_data = train_gen.flow_from_directory(
        train_dir,
        target_size=(IMG_SIZE, IMG_SIZE),
        batch_size=BATCH_SIZE,
        class_mode="categorical",
        subset="validation",
        seed=42,
    )
else:
    train_gen = ImageDataGenerator(
        rescale=1./255,
        rotation_range=15,
        zoom_range=0.1,
        horizontal_flip=True,
    )
    val_gen = ImageDataGenerator(rescale=1./255)

    train_data = train_gen.flow_from_directory(
        train_dir,
        target_size=(IMG_SIZE, IMG_SIZE),
        batch_size=BATCH_SIZE,
        class_mode="categorical",
    )

    val_data = val_gen.flow_from_directory(
        val_dir,
        target_size=(IMG_SIZE, IMG_SIZE),
        batch_size=BATCH_SIZE,
        class_mode="categorical",
    )

print(f"Using dataset at: {dataset_dir}")
print(f"Train images: {train_count} | Val images: {val_count if not use_split else int(train_count*0.2)}")
print(f"Classes: {train_data.class_indices}")

# -------------------
# MODEL (TRANSFER LEARNING)
# -------------------
base_model = MobileNetV2(
    weights="imagenet",
    include_top=False,
    input_shape=(IMG_SIZE, IMG_SIZE, 3)
)

base_model.trainable = False  # freeze base model

x = base_model.output
x = GlobalAveragePooling2D()(x)
x = Dense(128, activation="relu")(x)
x = Dropout(0.3)(x)
output = Dense(train_data.num_classes, activation="softmax")(x)

model = Model(inputs=base_model.input, outputs=output)

# -------------------
# COMPILE
# -------------------
model.compile(
    optimizer="adam",
    loss="categorical_crossentropy",
    metrics=["accuracy"]
)

model.summary()

# -------------------
# TRAIN
# -------------------
early_stop = EarlyStopping(monitor="val_loss", patience=3)

history = model.fit(
    train_data,
    validation_data=val_data,
    epochs=EPOCHS,
    callbacks=[early_stop]
)

# -------------------
# SAVE MODEL
# -------------------
model.save("alzheimer_cnn_model.h5")

# -------------------
# PLOT RESULTS
# -------------------
plt.plot(history.history["accuracy"], label="Train Accuracy")
plt.plot(history.history["val_accuracy"], label="Val Accuracy")
plt.legend()
plt.title("Model Accuracy")
plt.show()
