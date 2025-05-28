from ultralytics import YOLO
import cv2, os, sys, argparse

# ── 1) 인자 파싱 ────────────────────────────────
ap = argparse.ArgumentParser()
ap.add_argument("--image", required=True, help="분석할 이미지 절대경로")
args = ap.parse_args()

# ── 2) 가중치 절대경로(스크립트 기준) ────────────
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
WEIGHT   = os.path.join(BASE_DIR, "best.pt")
if not os.path.exists(WEIGHT):
    print(f"error: weight file not found → {WEIGHT}")
    sys.exit(1)

# ── 3) 모델 로드 & 이미지 읽기 ──────────────────
model  = YOLO(WEIGHT)
image  = cv2.imread(args.image)
if image is None:
    print("error: cannot read image")
    sys.exit(1)

# ── 4) 추론 ─────────────────────────────────────
results = model(image, verbose=False)

# 가장 신뢰도 높은 박스 1개만 사용
best = max(results[0].boxes, key=lambda b: float(b.conf[0]))
cls_id     = int(best.cls[0])
confidence = float(best.conf[0])
category   = model.names[cls_id]

# ── 5) 분리수거 안내 매핑 ───────────────────────
DISPOSAL = {
    "can":      "캔 전용 수거함에 버려주세요.",
    "plastic":  "플라스틱 전용 수거함에 버려주세요.",
    "paper":    "종이류 전용 수거함에 버려주세요.",
}
disposal = DISPOSAL.get(category, "일반 쓰레기통에 버려주세요.")

# ── 6) Java 파싱 포맷대로 3줄 출력 ──────────────
print(category)           # 1줄
print(f"{confidence:.4f}")# 2줄
print(disposal)           # 3줄
