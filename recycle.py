from ultralytics import YOLO
import cv2

# 모델 로드 (YOLOv8의 best.pt)
model = YOLO('best.pt')  # YOLOv8에서 훈련한 모델

# 이미지 경로 지정
image_path = 'test.jpg'

# 이미지 불러오기
image = cv2.imread(image_path)

# 추론 실행
results = model(image)

# 결과 확인 및 출력
for result in results:
    boxes = result.boxes
    for box in boxes:
        cls_id = int(box.cls[0])  # 클래스 ID
        conf = float(box.conf[0])  # confidence
        label = model.names[cls_id]  # 클래스 이름
        print(f"감지된 객체: {label}")
        
        # 바운딩 박스 좌표
        x1, y1, x2, y2 = map(int, box.xyxy[0])
        cv2.rectangle(image, (x1, y1), (x2, y2), (0, 255, 0), 2)
        cv2.putText(image, f'{label} {conf:.2f}', (x1, y1 - 10),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 255, 0), 2)

# 이미지 결과 보여주기
cv2.imshow('Detected', image)
cv2.waitKey(0)
cv2.destroyAllWindows()
