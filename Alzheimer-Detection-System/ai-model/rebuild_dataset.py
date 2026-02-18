import os
import shutil

OLD = "dataset"
NEW = "dataset_3class"

mapping = {
    "NonDemented": "Normal",
    "VeryMildDemented": "Early_Stage",
    "MildDemented": "Alzheimer",
    "ModerateDemented": "Alzheimer"
}


for split in ["train", "val", "test"]:
    for old_class, new_class in mapping.items():
        src = os.path.join(OLD, split, old_class)
        dst = os.path.join(NEW, split, new_class)

        if not os.path.exists(src):
            continue

        for img in os.listdir(src):
            src_file = os.path.join(src, img)
            dst_file = os.path.join(dst, img)

            if not os.path.exists(dst_file):
                shutil.copy(src_file, dst_file)

print("✅ Dataset rebuilt into 3 classes successfully.")
