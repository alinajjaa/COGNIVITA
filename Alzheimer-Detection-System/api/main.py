from fastapi import FastAPI, File, UploadFile
from fastapi.middleware.cors import CORSMiddleware
import tensorflow as tf
import numpy as np
from tensorflow.keras.preprocessing import image
from pathlib import Path
import shutil
import uuid

# -------------------
# APP INIT
# -------------------
app = FastAPI(title="Alzheimer MRI Detection API")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# -------------------
# CONFIG
# -------------------
IMG_SIZE = 224
CLASS_NAMES = ["Alzheimer", "Early_Stage", "Normal"]

BASE_DIR = Path(__file__).resolve().parent.parent
MODEL_PATH = BASE_DIR / "ai-model" / "alzheimer_cnn_3class.h5"
UPLOAD_DIR = BASE_DIR / "api" / "uploads"
UPLOAD_DIR.mkdir(exist_ok=True)

# -------------------
# LOAD MODEL (ONCE)
# -------------------
model = tf.keras.models.load_model(MODEL_PATH)
print("✅ Model loaded successfully")

# -------------------
# UTILS
# -------------------
def preprocess_image(img_path: Path):
    img = image.load_img(img_path, target_size=(IMG_SIZE, IMG_SIZE))
    img_array = image.img_to_array(img)
    img_array = img_array / 255.0
    img_array = np.expand_dims(img_array, axis=0)
    return img_array

# -------------------
# ROUTES
# -------------------
@app.get("/")
def root():
    return {"message": "Alzheimer MRI Detection API is running"}

@app.post("/predict")
async def predict(file: UploadFile = File(...)):
    # Save uploaded file
    file_ext = Path(file.filename).suffix
    file_name = f"{uuid.uuid4()}{file_ext}"
    file_path = UPLOAD_DIR / file_name

    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    # Preprocess & predict
    img_array = preprocess_image(file_path)
    preds = model.predict(img_array)[0]

    pred_index = int(np.argmax(preds))
    pred_class = CLASS_NAMES[pred_index]
    confidence = float(preds[pred_index] * 100)

    # Cleanup
    file_path.unlink(missing_ok=True)

    return {
        "prediction": pred_class,
        "confidence": round(confidence, 2),
        "probabilities": {
            CLASS_NAMES[i]: round(float(preds[i] * 100), 2)
            for i in range(len(CLASS_NAMES))
        }
    }
