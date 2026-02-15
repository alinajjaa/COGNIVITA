# AI Model Training

Quick steps to train the Alzheimer detection model.

## Setup

```powershell
# From repo root
& C:/Alzheimer-Detection-System/ai-model/venv/Scripts/Activate.ps1
python -m pip install --upgrade pip
pip install -r ai-model/requirements.txt
```

## Run training

```powershell
cd ai-model
python train_model.py
```

If you run from another directory:

```powershell
python "C:/Alzheimer-Detection-System/ai-model/train_model.py"
```

The script auto-detects the dataset at either `ai-model/dataset` or `ai-model/venv/dataset`.
