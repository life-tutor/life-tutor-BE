# [IT-ING] IT 취준생 직장인 커뮤니티 웹 어플리케이션

### **[IT-ING 구경 가기👉🏻](https://it-ing.co.kr)**

### 📌 GITHUB

 [Front-End Git-Hub 둘러보러 가기👉🏻](https://github.com/life-tutor/life-tutor-FE)

## IT 취준생 직장인 커뮤니티 웹 어플리케이션

![cover2-3](https://user-images.githubusercontent.com/107831692/190368971-8a70c5cc-09bc-4317-accc-44b13441f7ae.jpg)


---

## 📚 아키텍쳐

![Untitled](https://user-images.githubusercontent.com/107831692/190369013-4eb10bbb-06e7-406b-9ff5-b60da5dea36e.png)


---

## 👋 IT-ING의 핵심 기능

### 1 : 1 채팅방 기능  🔢

### 게시글 해시태그 등록, 검색 및 검색어 자동완성 ✨

### 소셜로그인 🗨️

### 작성한 글, 작성한 댓글 모아보기 기능 🗨️

### 개인정보 변경✨

### 게시글 및 댓글 공감기능🔢

---

## 🔧 기술적 의사 결정


| 사용 기술 | 기술 설명 |
| --- | --- |
| MySQL | 각 테이블마다 다양한 연관관계를 맺고 있기 때문에 효율적인 서버관리를 위하여 관계형 DB를 선택하였다. 그리고 MySQL은 가장 널리 사용되고 있는 관계형 데이터베이스인 만큼 다양한 유저 경험과 레퍼런스가 많아 개발하기 용이하다고 판단했다. |
| webSocket, STOMP | wss를 이용한 실시간 데이터 통신으로서, 실시간으로 유저들이 채팅을 할 수 있다. STOMP는 publish / subscribe 기반으로 동작하는데 메시지 송신, 수신에 대한 처리를 명확하게 정의할 수 있다. 또한 WebSocketHandler를 직접 구현할 필요 없이 @MessageMapping 같은 어노테이션을 사용해서 메시지 발행 시 엔드포인트를 별도로 분리해 관리할 수 있기 때문에 클라이언트와 서버가 통신하는 데 용이하다고 판단했다. |
| github actions | github에서 출시한 CI/CD tool로, 다른 환경의 설정들이 정의되어 있기 때문에 쉽고 간단하게 CI/CD에 대한 구현이 가능하다. |
| nginx | 서버 프록시 관리, https 배포를 위해 NGINX를 사용하였다. |
| refreshToken | jwt access token은 기본적으로 stateless하다. -> 누군가가 token을 탈취해서 사용한다면 알 수 있는 방법이 없다. 이 문제를 access token의 만료기간을 짧게 하는 방식으로 해결. 그 만큼 사용자는 access token을 재발급 받아야 함 -> 사용자 편의성 감소. 이를 해결하고자 refresh token을 만들고 그것으로 access token을 재발급 해준다. refresh token은 access token이 만료되었을때만 사용하기 때문에 만료기간이 길고 탈취당할 위험이 적다. |

---

## 😵‍💫 트러블슈팅

### ✅ 해시태그 검색기능 개선

**요구사항**

해시태그로 게시글, 채팅방을 검색할 수 있도록 구현하면서 like %keyword%를 사용했다가 index를 참조하지 않는 full scan이라 데이터양이 많아질 경우 속도가 느려질 것이 우려됨

**선택지**

1. MySQL의 full text search를 사용한다.
2. ELK Stack
3. query를 단순 조회(페치 조인으로)하고 JAVA에서 알고리즘으로 구현

**의견 조율**

1. 검색 해시태그가 띄어쓰기 없는 단어이고, 참조할 index table이 생성되면서 데이터베이스에 부담이 될 수 있다. 또한 insert 시 index가 추가로 생성되며 느려질 게 우려되어 기각.
2. Elasticsearch와 Logstash, Kibana(로그 시각화 및 관리)를 활용한 기술로, 실시간에 가까운 검색을 제공하는 등 오픈소스 검색엔진으로 가장 먼저 떠올린 기술이다. 하지만 현 프로젝트에서는 검색 기능이 주요 핵심 기능이 아니라 오버 엔지니어링으로 판단되어 기각.

**의견 결정**

1. ELK 스택에 대해서는 앞으로 알아두면 좋을 기술이고 현업에서 활발하게 쓰이는만큼 공부해두면 좋겠다는 의견은 일치하였지만, 공부가 필요한 부분인만큼 프로젝트에 바로 적용하기엔 (납품일이 정해져있으므로)한계가 있다고 판단하여 추후 공부하며 개선해나갈 예정이다.
2. 데이터베이스의 쿼리 실행이 빠르고 코드의 가독성이 좋아지면서 유지보수 측면에서도 좋을 것이라 판단되었다. 또한 하이버네이트 2차 쿼리 캐시를 활용하여 Api polling을 할 때마다 쿼리가 실행되지 않도록 개선하는 방향으로 결정하였다.

---

## 👩🏻‍💻제작자들🧑🏻‍💻

|포지션|이름|담당|깃허브|
|---|---|---|---|
|Back-End_(Spring)|문철현|로그인,회원가입, 마이페이지, 리프레시 토큰|[https://github.com/MoonDoorKing](https://github.com/MoonDoorKing)|
|Back-End_(Spring)|안병규|게시글 API, NGINX 구현 및 설정, CI/CD|[https://github.com/fox9d](https://github.com/fox9d)|
|Back-End_(Spring)|박주영|채팅, 게시글 공감, 댓글, 댓글 공감, 채팅방 해시태그 검색(하이버네이트 2차 캐시 사용)|[https://github.com/ju-ei8ht](https://github.com/ju-ei8ht)|
|Front-End_(React)|권익주|게시글 불러오기, 게시글 작성, 채팅 기능, 내가 쓴 글 기능 구현, 게시글/채팅방 해시태그 기반 검색 기능, 검색어 자동완성, HTTPS배포, 리프레시 토큰 구현|[https://github.com/nggoong](https://github.com/nggoong)|
|Front-End_(React)|설승운|게시글 상세페이지, 게시글 수정&삭제&공감, 댓글 작성&수정&삭제&공감, 마이페이지, 마이페이지 개인정보 변경, 비밀번호 변경, 댓글 단 글|[https://github.com/s-woon](https://github.com/s-woon)|
|Front-End_(React)|김다희|회원가입, 로그인 페이지, 소셜로그인|[https://github.com/uglad22](https://github.com/uglad22)|
|Designer|정유진|디자인 담당✨||
