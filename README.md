# message-system
Example projects for the Backend Development online course  
https://fastcampus.co.kr/dev_online_chat

* **Part2 채팅 뼈대 프로젝트 개발**
  * **Ch1 테스트에 대한 이야기**  
    4\. Groovy Console 소개
    * part2/ch1/04-preview-groovy-console
    * 참고: IntelliJ의 Groovy Console 소개 - https://prostars.net/361
    
    5\. Spock 사용을 위한 Groovy 기본 문법
    * part2/ch1/05-preview-groovy-for-spock

    6\. JUnit과 Spock의 차이
    * part2/ch1/06-2-preview-spock
    * part2/ch1/06-3-preview-spock
    
    7\. 스프링 기반 통합 테스트
    * part2/ch1/07-1-preview-spock-with-springboot
    * part2/ch1/07-2-preview-spock-with-springboot
  * **Ch2 Rest API와 WebSocket의 기본**  
    2\. 실시간 통신 프로젝트의 시작 핑퐁
    * part2/ch2/02-preview-restapi
  
    4\. 웹소켓으로 가능한 하나의 요청, 두 개의 응답
    * part2/ch2/04-preview-websocket

    6\. 채팅 프로젝트 시작. 프로토콜 기반 다이렉트 메세지
    * part2/ch2/06-direct-chat

    8\. 채팅 프로젝트를 그룹 메시지로 확장하기
    * part2/ch2/08-group-chat
    * 참고: Spring의 ConcurrentWebSocketSessionDecorator 소개 - https://prostars.net/362

    9\. 페이크 콘솔 채팅 클라이언트 구현
    * part2/ch2/09-message-client      
* **Part3 채팅 서비스 핵심 기능 개발**
  * **Ch1 대화 기록 저장을 위한 데이터베이스 연동**  
    2\. Docker Compose를 사용한 로컬 데이터베이스 구성과 DB Client 설치
    * part3/ch1/02-preview-localdb

    4\. 채팅 프로젝트에 메시지 테이블 기반 대화의 기록 추가
    * part3/ch1/04-group-chat-using-db
  * **Ch2 대화 기록 저장을 위한 데이터베이스 연동**  
    2\. 스프링 시큐리티로 구현하는 Login, Logout
    * part3/ch2/02-security-login-logout

    3\. 웹소켓 연결에 인증 기능 추가
    * part3/ch2/03-security-login-logout-websocket

    4\. 사용자 등록과 인증을 위한 스프링 시큐리티 데이터베이스 연동
    * part3/ch2/04-session-using-db

    5\. 서버가 다운되면 세션 정보가 사라진다. 레디스를 도입하여 세션 정보 유지
    * part3/ch2/05-session-using-db-redis

    6\. 채팅 프로젝트에 인증 기능 추가
    * part3/ch2/06-1-group-chat-using-session
    * part3/ch2/06-2-group-chat-using-session
    * part3/ch2/06-3-message-client-using-session
  * **Ch3 채팅 프로젝트에 다이렉트 메시지와 그룹 메시지 추가**  
    2\. 채팅 프로젝트에 연결 요청, 수락, 거부, 삭제 처리 추가
    * part3/ch3/02-1-message-system-add-connection
    * part3/ch3/02-2-message-system-add-connection
    * part3/ch3/02-3-message-system-add-connection
    * part3/ch3/02-4-message-system-add-connection
    * part3/ch3/02-5-message-system-add-connection
    * part3/ch3/02-6-message-system-add-connection
    * part3/ch3/02-7-message-client-add-connection

    3\. 채팅 프로젝트에 다이렉트 메시지 생성 기능 추가
    * part3/ch3/03-1-message-system-add-channel
    * part3/ch3/03-2-message-system-add-channel
    * part3/ch3/03-3-message-client-add-channel

    4\. 채팅 프로젝트에 그룹 메시지 생성, 초대 기능 추가
    * part3/ch3/04-message-system-add-channel

    5\. 채팅 프로젝트에 채팅방 목록 보기와 참여, 나가기 추가
    * part3/ch3/05-1-message-system-add-channel
    * part3/ch3/05-2-message-client-add-channel

  * **Ch4 오프라인 사용자에게도 메시지 전달을 위한 푸시 알림 기능**  
    3\. 채팅 프로젝트에 오프라인 사용자를 위한 푸시 알림 기능 추가
    * part3/ch4/03-message-system-add-push
