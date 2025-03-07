import torch
from ultralytics import YOLO

# 📌 학습된 YOLOv8 모델 불러오기
model = YOLO("best.pt")  # 'best.pt'는 학습된 모델 파일

# 📌 YOLO 클래스 → 분리수거 항목 매핑 테이블 (한글 기준)
waste_label_mapping = {
    "페트병": "페트병",
    "플라스틱": "플라스틱",
    "캔": "캔",
    "유리병": "유리병",
    "박스": "박스",
    "종이": "종이",
    "일반 쓰레기": "일반 쓰레기",
    "스티로폼": "스티로폼",
    "비닐": "비닐"
}

def get_waste_category(yolo_label):
    """ YOLO 라벨을 분리수거 카테고리로 변환 (한글 기준) """
    return waste_label_mapping.get(yolo_label, "일반 쓰레기")

def predict_waste_category(image_path):
    """ 학습된 모델을 사용하여 이미지 속 분리수거 품목 예측 """
    results = model(image_path)

    detected_items = set()

    print("📌 YOLO 클래스 리스트:", model.names)  # YOLO 클래스 확인

    for result in results:
        for box in result.boxes:
            class_id = int(box.cls.item())  # Tensor에서 정수 변환
            label_name = model.names.get(class_id, "Unknown")  # YOLO 클래스 ID -> 라벨
            
            print(f"🔹 감지된 클래스 ID: {class_id}")
            print(f"🔸 YOLO에서 감지한 라벨: {label_name}")

            label = get_waste_category(label_name)  # 변환된 분리수거 카테고리
            print(f"✅ 최종 변환된 카테고리: {label}")

            detected_items.add(label)

    return list(detected_items) if detected_items else ["일반 쓰레기"]

# 📌 테스트 이미지 예측
image_path = "test6.jpg"  # 테스트할 이미지 파일 경로
result = predict_waste_category(image_path)

print("🔍 탐지된 분리수거 품목:", result)
