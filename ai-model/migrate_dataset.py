"""
Migrate 4-class dataset to 3-class dataset with target image counts.

Mapping:
  Non_Demented -> Normal (2560 images)
  Mild_Demented -> Early_Stage (896 images)
  Very_Mild_Demented -> Early_Stage + Alzheimer (2240 images split)
  Moderate_Demented -> Alzheimer (64 images)

Target distribution:
  Normal: 2560 images
  Early_Stage: 1792 images (896 Mild + 896 Very_Mild)
  Alzheimer: 769 images (64 Moderate + 705 Very_Mild)

Train/Val/Test split: 70% / 15% / 15%
"""

import shutil
from pathlib import Path
from sklearn.model_selection import train_test_split
import random
import os

script_dir = Path(__file__).resolve().parent
source_dataset = script_dir / "venv" / "dataset" / "train"
target_dataset = script_dir / "dataset_3class"

if not source_dataset.exists():
    raise FileNotFoundError(f"Source dataset not found: {source_dataset}")

# Target counts for each class
TARGET_COUNTS = {
    "Normal": 2560,
    "Early_Stage": 1792,
    "Alzheimer": 769,
}

# Mapping from source classes to target classes with desired counts
CLASS_MAPPING = {
    "Non_Demented": [("Normal", 2560)],
    "Mild_Demented": [("Early_Stage", 896)],
    "Very_Mild_Demented": [("Early_Stage", 896), ("Alzheimer", 705)],  # 896 + 705 = 1601, but we have 2240
    "Moderate_Demented": [("Alzheimer", 64)],
}

# Get all image files from source
def get_images(class_dir):
    """Get all image files from a class directory."""
    if not class_dir.exists():
        return []
    exts = {".jpg", ".jpeg", ".png", ".bmp"}
    return [f for f in class_dir.rglob("*") if f.is_file() and f.suffix.lower() in exts]

# Collect all images by source class
print("Collecting source images...")
source_images = {}
for source_class in CLASS_MAPPING.keys():
    class_dir = source_dataset / source_class
    images = get_images(class_dir)
    source_images[source_class] = images
    print(f"  {source_class}: {len(images)} images")

# Migrate images
print("\nMigrating images to 3-class structure...")
splits = {"train": 0.7, "val": 0.15, "test": 0.15}

for source_class, mappings in CLASS_MAPPING.items():
    images = source_images[source_class]
    random.shuffle(images)  # Randomize before splitting
    
    offset = 0
    for target_class, count in mappings:
        # Get the subset for this target class
        subset = images[offset : offset + count]
        offset += count
        
        if not subset:
            print(f"  ⚠️  No images for {source_class} -> {target_class}")
            continue
        
        # Split into train/val/test
        train_count = int(len(subset) * splits["train"])
        val_count = int(len(subset) * splits["val"])
        
        train_imgs = subset[:train_count]
        val_imgs = subset[train_count : train_count + val_count]
        test_imgs = subset[train_count + val_count:]
        
        # Copy to target directories
        for split_name, split_imgs in [("train", train_imgs), ("val", val_imgs), ("test", test_imgs)]:
            target_dir = target_dataset / split_name / target_class
            target_dir.mkdir(parents=True, exist_ok=True)
            
            for src_img in split_imgs:
                dst_img = target_dir / src_img.name
                shutil.copy2(src_img, dst_img)
        
        print(f"  {source_class} ({len(subset)}) -> {target_class}: "
              f"train={len(train_imgs)}, val={len(val_imgs)}, test={len(test_imgs)}")

# Verify final counts
print("\nVerifying final counts...")
for split in ["train", "val", "test"]:
    print(f"\n{split.upper()}:")
    for target_class in TARGET_COUNTS.keys():
        class_dir = target_dataset / split / target_class
        count = len(get_images(class_dir))
        print(f"  {target_class}: {count}")

print("\n✓ Migration complete!")
