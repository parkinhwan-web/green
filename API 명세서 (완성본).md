api 명세서

---
<br>
사용자 관리
<details>
<summary>회원가입</summary>
1️⃣ 회원가입 API
📌 설명
사용자가 새로운 계정을 생성하는 API

📌 엔드포인트
Method: POST
URL: /users/signup
인증 필요 여부: ❌ (회원가입은 인증이 필요 없음)

📌 요청 예시 (Request Body)

json
{
  "username": "recycle_user",
  "email": "user@example.com",
  "password": "securepassword123"
}

📌 응답 예시 (Response Body)

json
{
  "message": "회원가입 성공!",
  "user_id": 123,
  "created_at": "2025-02-04T12:00:00Z"
}

📌 응답 코드 (HTTP Status Code)

201 Created → 회원가입 성공
400 Bad Request → 요청 데이터가 잘못됨
409 Conflict → 이미 가입된 이메일
</details>

<details>
<summary>로그인</summary>
2️⃣ 로그인 API

📌 설명

사용자가 로그인하고 JWT 토큰을 받는 API

📌 엔드포인트

Method: POST

URL: /users/login

인증 필요 여부: ❌ (로그인 요청이므로 필요 없음)

📌 요청 예시 (Request Body)

{
  "email": "user@example.com",
  "password": "securepassword123"
}

📌 응답 예시 (Response Body)

{
  "message": "로그인 성공!",
  "token": "eyJhbGciOiJIUzI1NiIsIn...",
  "expires_in": 3600
}

📌 응답 코드 (HTTP Status Code)

200 OK → 로그인 성공

401 Unauthorized → 인증 실패 (잘못된 이메일 또는 비밀번호)
</details>

<details>
<summary>로그아웃</summary>
3️⃣ 로그아웃 API

📌 설명

사용자가 로그아웃하여 JWT 토큰을 만료시키는 API

📌 엔드포인트

Method: POST

URL: /users/logout

인증 필요 여부: ✅ (로그인된 사용자만 사용 가능)

📌 요청 헤더 (Headers)

{
  "Authorization": "Bearer {JWT_TOKEN}"
}

📌 응답 예시 (Response Body)

{
  "message": "로그아웃 성공!"
}

📌 응답 코드 (HTTP Status Code)

200 OK → 로그아웃 성공

401 Unauthorized → 인증 실패 (JWT 토큰 없음)
</details>


<details>
<summary>사용자 정보 조회</summary>
2️⃣ 사용자 정보 조회 API

📌 설명

사용자의 정보를 조회하는 API

📌 엔드포인트

Method: GET

URL: /users/profile

인증 필요 여부: ✅ (로그인된 사용자만 사용 가능)

📌 요청 헤더 (Headers)

{
  "Authorization": "Bearer {JWT_TOKEN}"
}

📌 응답 예시 (Response Body)

{
  "id": 123,
  "email": "user@example.com", 
  "username": "사용자닉네임",
  "points": 1500,
  "recycleCount": 42,
  "enabled": true,
  "accountNonExpired": true,
  "accountNonLocked": true,
  "credentialsNonExpired": true,
  "authorities": ["ROLE_USER"]
}


📌 응답 코드 (HTTP Status Code)

200 OK → 조회 성공

401 Unauthorized → 인증 실패 (JWT 토큰 없음)

404 Not Found → 해당 사용자 없음
</details>

<details>
<summary>사용자 정보 수정</summary>
3️⃣ 사용자 정보 수정 API

📌 설명

사용자가 자신의 정보를 수정하는 API

📌 엔드포인트

Method: PUT

URL: /users/{user_id}

인증 필요 여부: ✅ (로그인된 사용자만 사용 가능)

📌 요청 헤더 (Headers)

{
  "Authorization": "Bearer {JWT_TOKEN}"
}

📌 요청 예시 (Request Body)

{
  "username": "new_username",
  "email": "new_email@example.com"
}

📌 응답 예시 (Response Body)

{
  "message": "사용자 정보 수정 성공!"
}

📌 응답 코드 (HTTP Status Code)

200 OK → 수정 성공

400 Bad Request → 요청 데이터 오류

401 Unauthorized → 인증 실패 (JWT 토큰 없음)

404 Not Found → 해당 사용자 없음
</details>

<details>
<summary>계정 삭제</summary>
4️⃣ 계정 삭제 API

📌 설명

사용자가 자신의 계정을 삭제하는 API

📌 엔드포인트

Method: DELETE

URL: /users/{user_id}

인증 필요 여부: ✅ (로그인된 사용자만 사용 가능)

📌 요청 헤더 (Headers)

{
  "Authorization": "Bearer {JWT_TOKEN}"
}

📌 응답 예시 (Response Body)

{
  "message": "계정 삭제 성공!"
}

📌 응답 코드 (HTTP Status Code)

200 OK → 삭제 성공

401 Unauthorized → 인증 실패 (JWT 토큰 없음)

404 Not Found → 해당 사용자 없음
</details>

<br>
분리수거 분류 시스템
<details>
<summary>이미지 업로드 및 분석 요청</summary>
2️⃣ 이미지 업로드 및 분석 요청 API

📌 설명

사용자가 이미지를 업로드하고 AI 모델을 통해 분석 요청을 보내는 API

📌 엔드포인트

Method: POST

URL: /recycle/analyze

인증 필요 여부: ✅ (로그인된 사용자만 사용 가능)

📌 요청 헤더 (Headers)

{
  "Authorization": "Bearer {JWT_TOKEN}",
  "Content-Type": "multipart/form-data"
}

📌 요청 예시 (multipart/form-data)

image: {업로드된 이미지 파일}

📌 응답 예시 (Response Body)

{
  "analysis_id": 456,
  "status": "processing",
  "message": "이미지 분석 요청이 접수되었습니다.",
  "created_at": "2025-02-04T12:10:00Z"
}

📌 응답 코드 (HTTP Status Code)

202 Accepted → 분석 요청 성공

400 Bad Request → 잘못된 요청

401 Unauthorized → 인증 실패 (JWT 토큰 없음)

500 Internal Server Error → 서버 오류
</details>

<details>
<summary>분석 결과 조회</summary>
3️⃣ 분석 결과 조회 API

📌 설명

AI 모델이 분석한 결과를 조회하는 API

📌 엔드포인트

Method: GET

URL: /recycle/result/{analysis_id}

인증 필요 여부: ✅ (로그인된 사용자만 사용 가능)

📌 요청 헤더 (Headers)

{
  "Authorization": "Bearer {JWT_TOKEN}"
}

📌 응답 예시 (Response Body)

{
  "analysis_id": 456,
  "category": "플라스틱",
  "confidence": 0.95,
  "disposal_method": "플라스틱 전용 수거함에 버려주세요.",
  "created_at": "2025-02-04T12:15:00Z"
}

📌 응답 코드 (HTTP Status Code)

200 OK → 분석 결과 조회 성공

400 Bad Request → 잘못된 요청

401 Unauthorized → 인증 실패 (JWT 토큰 없음)

404 Not Found → 해당 분석 결과 없음

500 Internal Server Error → 서버 오류
</details>

<br>
사용자 활동 로그
<details>
<summary>분리수거 활동 기록</summary>
🔹 분리수거 활동 기록 API

📌 설명

사용자가 분리수거 활동을 기록하는 API(기록해야만 랭킹에 반영, 아직 ai와 연동 불가로 body에 데이터 삽입해야 함)

📌 엔드포인트

Method: POST

URL: /recycle/log

인증 필요 여부: ✅ (로그인된 사용자만 사용 가능)

📌 요청 예시 (Request Body)

{
  "analysis_id": 456,
  "disposal_category": "플라스틱",
  "disposal_method": "플라스틱 전용 수거함에 버려주세요."
}

📌 응답 예시 (Response Body)

{
    "success": true,
    "logId": 10,
    "pointsEarned": 50,
    "totalPoints": 1600,
    "recycleCount": 4,
    "message": "null 분리수거가 기록되었습니다.",
    "wasteTypeKorean": null,
    "rankChange": {
        "rank_improved": false,
        "previous_rank": 1,
        "current_rank": 1
    }
}

📌 응답 코드 (HTTP Status Code)

201 Created → 기록 성공

400 Bad Request → 잘못된 요청

401 Unauthorized → 인증 실패 (JWT 토큰 없음)
</details>


<details>
<summary>분리수거 활동 조회</summary>
🔹 분리수거 활동 조회 API

📌 설명

사용자의 분리수거 활동을 조회하는 API

📌 엔드포인트

Method: GET

URL: /recycle/log/{user_id}

인증 필요 여부: ✅ (로그인된 사용자만 사용 가능)

📌 응답 예시 (Response Body)

[
  {
    "log_id": 789,
    "analysis_id": 456,
    "disposal_category": "플라스틱",
    "disposal_method": "플라스틱 전용 수거함에 버려주세요.",
    "created_at": "2025-02-04T12:20:00Z"
  }
]

📌 응답 코드 (HTTP Status Code)

200 OK → 조회 성공

401 Unauthorized → 인증 실패 (JWT 토큰 없음)

404 Not Found → 기록 없음
</details>

<details>
<summary>분리수거 활동 삭제</summary>
🔹 분리수거 활동 삭제 API

📌 설명

사용자가 특정 분리수거 활동 기록을 삭제하는 API

📌 엔드포인트

Method: DELETE

URL: /recycle/log/{log_id}

인증 필요 여부: ✅ (로그인된 사용자만 사용 가능)

📌 응답 예시 (Response Body)

{
  "message": "분리수거 활동 기록이 삭제되었습니다."
}

📌 응답 코드 (HTTP Status Code)

200 OK → 삭제 성공

401 Unauthorized → 인증 실패 (JWT 토큰 없음)

404 Not Found → 해당 기록 없음
</details>

<br>
리워드 시스템

<details>
<summary>포인트 조회</summary>
🔹 포인트 조회 API

📌 설명

사용자의 포인트를 조회하는 API

📌 엔드포인트

Method: GET

URL: /users/{user_id}/points/history

인증 필요 여부: ✅ (로그인된 사용자만 사용 가능)

📌 요청 헤더 (Headers)

{
  "Authorization": "Bearer {JWT_TOKEN}"
}

📌 응답 예시 (Response Body)

{
  "id": 78,
  "date": "2025-06-02T00:36:42.999964Z",
  "type": "적립",
  "reason": "AI 분석 리워드",
  "brandName": "플라스틱",
  "points": 1000,
  "balance": 2000
}

📌 응답 코드 (HTTP Status Code)

200 OK → 조회 성공

401 Unauthorized → 인증 실패 (JWT 토큰 없음)

404 Not Found → 사용자 없음
</details>

<details>
<summary>상품권 구매</summary>
🔹 상품권 구매 API

📌 설명

사용자가 포인트를 사용하여 상품권을 구매할 때 사용하는 API

📌 엔드포인트

Method: POST

URL: /shop/coupons/{couponId}/purchase

인증 필요 여부: ✅ (로그인된 사용자만 사용 가능)

📌 요청 헤더 (Headers)

{
  "Authorization": "Bearer {JWT_TOKEN}"
}


📌 응답 예시 (Response Body)

{
    "success": true,
    "message": "상품권 구매가 완료되었습니다.",
    "userCouponId": 7,
    "remainingPoints": 1500,
    "usedDate": null,
    "couponImageUrl": null,
    "couponImageBase64": null,
    "expireDate": "2025-06-16",
    "barcode": "1c6a3679-1dc2",
    "couponDetails": {
        "brandName": "CU",
        "productName": "ABC 초코쿠키쿠앤크",
        "pointsUsed": 1500,
        "barcode": null,
        "expireDate": null,
        "usageInstructions": null
    }
}

📌 응답 코드 (HTTP Status Code)

200 OK → 사용 성공

400 Bad Request → 요청 데이터 오류 (예: 포인트 부족)

401 Unauthorized → 인증 실패 (JWT 토큰 없음)

404 Not Found → 사용자 없음
</details>

<details>
<summary>쿠폰 사용</summary>
🔹 쿠폰 사용 API

📌 설명
사용자가 보유한 쿠폰을 사용하는 API

📌 엔드포인트

Method: POST

URL: /shop/coupons/{couponId}/use

인증 필요 여부: ✅ (로그인된 사용자만 사용 가능)

📌 요청 헤더 (Headers)

{
  "Authorization": "Bearer {JWT_TOKEN}"
}
📌 요청 바디 (Request Body)

없음 (Body 비워도 작동함)

📌 응답 예시 (Response Body)

{
  "success": true,
  "message": "쿠폰이 성공적으로 발급되었습니다.",
  "userCouponId": 5,
  "remainingPoints": 0,
  "usedDate": "2025-06-02T11:01:47.2373278+09:00",
  "couponImageUrl": "없음",
  "couponImageBase64": null,
  "expireDate": null,
  "barcode": null,
  "couponDetails": {
    "brandName": "CU",
    "productName": "ABC 초코쿠키쿠앤크",
    "pointsUsed": 0,
    "barcode": "e224cbcb-2886",
    "expireDate": "2025-06-16",
    "usageInstructions": "매장에서 이 쿠폰 이미지를 제시해주세요"
  }
}

📌 응답 코드 (HTTP Status Code)

200 OK → 사용 성공

403 Forbidden → 사용자 권한 없음

404 Not Found → 쿠폰이 존재하지 않음 또는 소유하지 않음

409 Conflict → 이미 사용된 쿠폰
</details>


<details>
<summary>상품권 목록 조회</summary>
🔹 상품권 목록 API

📌 설명
사용자가 현재 구매 가능한 상품권 목록을 조회하는 API

📌 엔드포인트

Method: GET

URL: /shop/coupons

인증 필요 여부: ✅ (로그인된 사용자만 사용 가능)

📌 요청 헤더 (Headers)

{
  "Authorization": "Bearer {JWT_TOKEN}"
}
📌 요청 바디 (Request Body)

없음

📌 응답 예시 (Response Body)

[
  {
    "id": 1,
    "brandName": "CU",
    "productName": "ABC 초코쿠키쿠앤크",
    "points": 1500,
    "category": "식품",
    "imageUrl": "없음",
    "expireDays": 14,
    "description": "2점! 너무 달다",
    "amount": 1500
  },
  {
    "id": 2,
    "brandName": "GS25",
    "productName": "오뚜기순후추팝콘",
    "points": 1700,
    "category": "식품",
    "imageUrl": "없음",
    "expireDays": 14,
    "description": "1점! 음식 가지고 장난?",
    "amount": 1700
  }
]
📌 응답 코드 (HTTP Status Code)

200 OK → 조회 성공

401 Unauthorized → 인증 실패 (JWT 토큰 없음)
</details>


<details>
<summary>사용자 쿠폰함 조회</summary>
🔹 쿠폰함 조회 API

📌 설명
로그인한 사용자의 미사용 및 사용된 쿠폰 목록을 조회하는 API

📌 엔드포인트

Method: GET

URL: /users/{user_id}/coupons

인증 필요 여부: ✅ (로그인된 사용자만 사용 가능)

📌 요청 헤더 (Headers)

{
  "Authorization": "Bearer {JWT_TOKEN}"
}
📌 요청 바디 (Request Body)

없음

📌 응답 예시 (Response Body)

{
  "unusedCoupons": [
    {
      "id": 7,
      "brandName": "CU",
      "productName": "ABC 초코쿠키쿠앤크",
      "imageUrl": "없음",
      "purchaseDate": "2025-06-02",
      "expireDate": "2025-06-16",
      "daysRemaining": 14,
      "barcode": "1c6a3679-1dc2",
      "usedDate": null,
      "pointsUsed": null
    }
  ],
  "usedCoupons": [
    {
      "id": 5,
      "brandName": "스타벅스",
      "productName": "아메리카노 T",
      "imageUrl": "없음",
      "purchaseDate": "2025-05-25",
      "expireDate": "2025-06-08",
      "daysRemaining": 0,
      "barcode": "a7d1bc92-33ff",
      "usedDate": "2025-06-01T12:30:00+09:00",
      "pointsUsed": 4500
    }
  ]
}
📌 응답 코드 (HTTP Status Code)

200 OK → 조회 성공

401 Unauthorized → 인증 실패 (JWT 토큰 없음)

404 Not Found → 사용자 또는 쿠폰 없음
</details>


<br>
앱 설정

<details>
<summary>앱 설정 조회</summary>
🔹 앱 설정 조회 API

📌 설명

앱의 환경설정을 조회하는 API

📌 엔드포인트

Method: GET

URL: /users/settings

인증 필요 여부: ✅ (로그인된 사용자만 사용 가능)

📌 요청 헤더 (Headers)

{
  "Authorization": "Bearer {JWT_TOKEN}"
}

📌 응답 예시 (Response Body)

{
  "theme": "dark",
  "notifications": true,
  "language": "ko"
}

📌 응답 코드 (HTTP Status Code)

200 OK → 조회 성공

401 Unauthorized → 인증 실패 (JWT 토큰 없음)
</details>

<details>
<summary>앱 설정 변경</summary>
🔹 앱 설정 변경 API

📌 설명

사용자가 앱의 환경설정을 변경하는 API

📌 엔드포인트

Method: PUT

URL: /users/settings

인증 필요 여부: ✅ (로그인된 사용자만 사용 가능)

📌 요청 헤더 (Headers)

{
  "Authorization": "Bearer {JWT_TOKEN}"
}

📌 요청 예시 (Request Body)

{
  "theme": "light",
  "notifications": false,
  "language": "en"
}

📌 응답 예시 (Response Body)

{
  "message": "설정이 변경되었습니다.",
  "updated_at": "2025-02-04T12:40:00Z"
}

📌 응답 코드 (HTTP Status Code)

200 OK → 변경 성공

400 Bad Request → 요청 데이터 오류

401 Unauthorized → 인증 실패 (JWT 토큰 없음)
</details>