import tensorflow as tf
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.metrics import confusion_matrix, classification_report
from tensorflow.keras.preprocessing.image import ImageDataGenerator

# -------------------
# LOAD MODEL
# -------------------
model = tf.keras.models.load_model("alzheimer_cnn_3class.h5")

# -------------------
# TEST DATA
# -------------------
IMG_SIZE = 224
BATCH_SIZE = 32

test_gen = ImageDataGenerator(rescale=1./255)

test_data = test_gen.flow_from_directory(
    "dataset_3class/test",
    target_size=(IMG_SIZE, IMG_SIZE),
    batch_size=BATCH_SIZE,
    class_mode="categorical",
    shuffle=False
)

# -------------------
# PREDICTION
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
            cmap="Greens")

plt.xlabel("Predicted")
plt.ylabel("Actual")
plt.title("Confusion Matrix (3 Classes)")
plt.show()

# -------------------
# REPORT
# -------------------
print("\nClassification Report:\n")
print(classification_report(true_classes, pred_classes, target_names=class_names))
