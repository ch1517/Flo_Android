# 프로그래머스 [앱-Android] 뮤직 플레이어 앱
프로그래머스 실력 체크 과제(https://programmers.co.kr/skill_check_assignments/3)
## Video Example
썸네일 클릭 시 Youtube 링크로 이동합니다.

<a href="https://www.youtube.com/watch?v=2RMWmXIJ45o"><img src="https://user-images.githubusercontent.com/25702775/116055829-174c4600-a6b8-11eb-9f7e-aecb4eb7cfab.jpg" width="100%"></a>

## Introduce Application
프로그래머스 안드로이드 어플리케이션 개발 과제를 수행한 결과물 입니다.<br>
API 호출을 이용해서 JSON 타입의 음악 정보를 수신하여, 음악 재생 기능을 수행합니다.

페이지는 음악 재생 화면과 전체 가사보기 화면과 같이 두 개의 화면으로 구성되어 있습니다.<br>
음악 재생 화면에서는 음악의 제목, 앨범명, 아티스트명, 앨범 아트와 현재 재생 중인 가사를 나타내는 작은 가사화면이 있습니다.<br>
하단의 재생바를 이용해서 재생 구간을 변경할 수 있습니다.

전체 가사보기 화면은 Seek 모드 On/Off에 따라서 화면 구성이 달라집니다.<br>
먼저 Seek 모드 Off에서는 현재 재생 중인 가사를 ‘검은색 굵은 글자’로 표시하며, 화면의 1/2를 벗어나면 현재 재생 가사를 중앙으로 오도록 가사를 따라가며 화면이 갱신됩니다. 가사 화면을 터치하면, 전체 가사보기 화면이 닫힙니다.

Seek 스위치를 통해서 Seek모드를 On으로 변경한 경우 현재 재생 중인 가사는 ‘파란색 굵은 글자’로 표시됩니다.<br>
Seek On 모드에서는 가사를 터치하면 해당 가사 구간으로 재생 위치가 변경됩니다.<br>
가사 터치의 용의성을 위해 해당 모드에서는 현재 재생 중인 가사를 하이라이팅(진하게 표시)만 해주며, 화면이 가사를 따라가지 않습니다.

## Reference Materials and Data
- **Data :** 곡 정보 데이터(https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/2020-flo/song.json) API 로드
- **Language : Kotlin**
- **Development Tool :** Android Studio
- **Version :**        
  - minSdkVersion 21
  - targetSdkVersion 27이상
  
## Requirement & Build Project
https://programmers.co.kr/assignment_tokens/1030913/code_view#
