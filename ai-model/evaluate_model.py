import tensorflow as tf
import numpy as np
import matplotlib.pyplot as plt
from sklearn.metrics import confusion_matrix, classification_report
import seaborn as sns
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from pathlib import Path
import os

# -------------------
# RESOLVE PATHS
# -------------------
script_dir = Path(__file__).resolve().parent
model_candidates = [
    script_dir / "alzheimer_cnn_model.h5",
    script_dir / "venv" / "alzheimer_cnn_model.h5",
]
model_path = next((p for p in model_candidates if p.exists()), model_candidates[0])

dataset_candidates = [
    script_dir / "dataset",
    script_dir / "venv" / "dataset",
]
dataset_dir = next((p for p in dataset_candidates if p.exists()), None)
if dataset_dir is None:
    raise FileNotFoundError(
        f"Dataset folder not found. Checked: {', '.join(str(p) for p in dataset_candidates)}"
    )

test_dir = str(dataset_dir / "test")

# -------------------
# LOAD MODEL
# -------------------
print(f"Loading model from: {model_path}")
model = tf.keras.models.load_model(str(model_path))

# -------------------
# DATA GENERATOR (TEST SET)
# -------------------
IMG_SIZE = 224
BATCH_SIZE = 32

def _count_images(root: Path) -> int:
    if not root.exists():
        return 0
    exts = {".jpg", ".jpeg", ".png", ".bmp"}
    count = 0
    for sub in root.rglob("*"):
        if sub.is_file() and sub.suffix.lower() in exts:
            count += 1
    return count

test_count = _count_images(Path(test_dir))

if test_count == 0:
    print("⚠️  No test images found. Using validation split from training data for evaluation.")
    train_dir = str(dataset_dir / "train")
    
    # Use the same split as training to get the validation set
    test_gen = ImageDataGenerator(
        rescale=1./255,
        validation_split=0.2
    )
    
    test_data = test_gen.flow_from_directory(
        train_dir,
        target_size=(IMG_SIZE, IMG_SIZE),
        batch_size=BATCH_SIZE,
        class_mode="categorical",
        subset="validation",
        shuffle=False,
        seed=42
    )
    print(f"Using validation subset from training data: {train_dir}")
else:
    test_gen = ImageDataGenerator(rescale=1./255)
    
    test_data = test_gen.flow_from_directory(
        test_dir,
        target_size=(IMG_SIZE, IMG_SIZE),
        batch_size=BATCH_SIZE,
        class_mode="categorical",
        shuffle=False
    )
    print(f"Using test dataset at: {test_dir}")

print(f"Found {test_data.samples} test images across {len(test_data.class_indices)} classes")

# -------------------
# PREDICTIONS
# -------------------
pred_probs = model.predict(test_data)
pred_classes = np.argmax(pred_probs, axis=1)
true_classes = test_data.classes
class_names = list(test_data.class_indices.keys())

# -------------------
# CONFUSION MATRIX
# -------------------
cm = confusion_matrix(true_classes, pred_classes)

plt.figure(figsize=(6, 6))
sns.heatmap(cm, annot=True, fmt="d",
            xticklabels=class_names,
            yticklabels=class_names,
            cmap="Blues")

plt.xlabel("Predicted")
plt.ylabel("Actual")
plt.title("Confusion Matrix")
plt.show()

# -------------------
# CLASSIFICATION REPORT
# -------------------
print("\nClassification Report:\n")
print(classification_report(true_classes, pred_classes, target_names=class_names))
