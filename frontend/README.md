# Tomo - android
친구들과의 우정을 확인하고 싶을 때 사용하면 좋은 서비스입니다.


### 폴더구조
```plaintext
app/
├── data/
│   ├── api/          # Retrofit, OkHttp, API Service 인터페이스
│   ├── repository/   # Repository 구현체
│   └── model/        # DTO, Response, Request 객체
├── domain/
│   ├── repository/   # Repository 인터페이스
│   ├── model/        # Domain 모델(Entity)
│   └── usecase/      # 유스케이스
├── ui/
│   ├── feature1/
│   └── feature2/
├── di/               # Hilt/Koin 모듈
├── navigation/       # 네비게이션 관련 코드
├── utils/            # 공통 Util
└── build.gradle
```
