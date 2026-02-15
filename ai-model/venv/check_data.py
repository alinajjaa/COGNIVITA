import os
from pathlib import Path

base_dir = Path(__file__).resolve().parent
base = str(base_dir / "dataset" / "train")
for cls in os.listdir(base):
    path = os.path.join(base, cls)
    print(cls, ":", len(os.listdir(path)))
