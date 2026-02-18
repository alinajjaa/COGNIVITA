import os

base = "dataset_3class/train"

for cls in os.listdir(base):
    print(cls, ":", len(os.listdir(os.path.join(base, cls))))
