import argparse
import tensorflow as tf
import numpy as np
from tensorflow.keras.preprocessing import image
from pathlib import Path


# -------------------
# CONFIG
# -------------------
IMG_SIZE = 224
CLASS_NAMES = ["Alzheimer", "Early_Stage", "Normal"]


def resolve_model_path(script_dir: Path) -> Path:
    candidates = [
        script_dir / "alzheimer_cnn_model_3class.h5",
        script_dir / "alzheimer_cnn_3class.h5",  # fallback if previously saved
        script_dir / "venv" / "alzheimer_cnn_model_3class.h5",
    ]
    for p in candidates:
        if p.exists():
            return p
    raise FileNotFoundError(f"Model not found. Checked: {', '.join(str(p) for p in candidates)}")


def load_and_preprocess(img_path: Path) -> np.ndarray:
    if not img_path.exists():
        raise FileNotFoundError(f"Image not found: {img_path}")
    img = image.load_img(img_path, target_size=(IMG_SIZE, IMG_SIZE))
    img_array = image.img_to_array(img) / 255.0
    return np.expand_dims(img_array, axis=0)


def main():
    parser = argparse.ArgumentParser(description="Predict Alzheimer stage for a single MRI image.")
    parser.add_argument("--image", required=True, help="Path to the image to predict")
    args = parser.parse_args()

    script_dir = Path(__file__).resolve().parent
    model_path = resolve_model_path(script_dir)
    img_path = Path(args.image)

    print(f"Loading model from: {model_path}")
    model = tf.keras.models.load_model(str(model_path))

    img_array = load_and_preprocess(img_path)

    predictions = model.predict(img_array)[0]
    predicted_index = np.argmax(predictions)
    predicted_class = CLASS_NAMES[predicted_index]
    confidence = predictions[predicted_index] * 100

    print("\n🧠 MRI ANALYSIS RESULT")
    print("----------------------")
    print(f"Predicted class : {predicted_class}")
    print(f"Confidence      : {confidence:.2f}%")

    print("\nFull probabilities:")
    for cls, prob in zip(CLASS_NAMES, predictions):
        print(f"{cls:12s}: {prob*100:.2f}%")


if __name__ == "__main__":
    main()
