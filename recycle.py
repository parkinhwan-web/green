from ultralytics import YOLO

# 📌 YOLO 모델 학습 (Transfer Learning)
model = YOLO("yolov8n.pt")  # 기본 YOLO 모델 로드

# 📌 Fine-Tuning (데이터셋을 활용한 학습)
model.train(data="C:/Users/user/Desktop/전설프2/dataset/dataset.yaml", epochs=50, imgsz=640, lr0=0.01)


