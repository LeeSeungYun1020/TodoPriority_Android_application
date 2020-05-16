# TodoPriority_Android_application
Term project (Computer_Software_Design_and_Lab_CP33987)


# 텀 프로젝트 제안서


우선 순위 기반 시간 관리 어플리케이션 개발

## 문제 정의 및 문제 해결 필요성

### 1.  시간 관리 문제

-   시간은 누구에게나 매일 24시간이 주어지며 일정한 속도로 진행되어 멈출 수
    없습니다. 나에게 주어진 시간만 사용할 수 있으며 다른 이에게 빌리거나 모아둘
    수 없습니다. 사용 방법, 시기에 따라 그 가치가 달라집니다.

-   복잡한 현대 사회에서 사람들의 주요 고민은 시간 관리 문제입니다. (오지현,
    2008) 이러한 시간적인 스트레스는 중요도와 긴급도에 따라 일을 처리할 수 있는
    시간관리 능력이 부족하기 때문에 발생합니다. 이러한 문제는 시간 계획을 세우고
    계획에 따라 생활하며 일을 순서에 따라 처리할 수 있는 시간 관리 능력이
    향상되어야 해결할 수 있습니다. 시간 관리는 시간적 스트레스를 유발하는 시간
    갈등과 시간 압박을 해결하는 한 방법이 될 수 있습니다. 시간이 유한하다는
    사실과 모든 활동에 꼭 사용되는 자원이라는 면에서 시간 관리의 중요성이 더욱
    강조됩니다. (오현진, 정지윤, 2006)

-   시간 관리를 어렵게 하는 요인 중 하나는 시간 관리 개념과 방법에 대한 인식
    부족입니다. 예를 들자면 "시간 관리는 상식적인 부분이며 나는 일을 잘하고 있기
    때문에 시간 관리에는 문제가 없다.", "나는 압력을 받을 때 일을 더 잘한다.
    그런데 시간 관리는 그러한 이점을 살리지 못한다.", "난 매우 창의적인 일을
    하기 때문에 일상적인 직무 스케줄에 따를 수 없다.", "시간 관리에는 많은
    일들이 필요하고 그런 일을 할 시간은 부족하다." (김영치, 2005) 같은 경우가
    있습니다.

-   고등학생을 대상으로 한 설문조사에서도 시간 관리 방법에 대한 내용을 찾아볼 수
    있습니다. 참여자의 85.8%가 시간 관리의 필요성을 느끼고 있으며, 79.9%는
    구체적인 시간 관리 방법을 모르겠다고 답했습니다. (김주리, 2012) 대부분의
    학생들이 시간 관리 필요성을 느끼고 있지만 구체적인 활용 방법을 모르고 있었던
    것입니다.

-   기존의 꽉 찬 일정을 시간 관리의 척도로 여겼던 관점에서 벗어나 자신의 목표와
    일의 중요도, 긴급도를 고려한 시간 활용의 효율성을 고려한 시간 관리로
    중요성의 인식이 전환되었습니다. (오지현, 2008)

### 2.  문제 해결 필요성

-   시간 관리에 대한 고민을 해결하기 위해 새로운 어플리케이션을 제안하고자
    합니다.

-   우리는 해야 할 일도 하고자 하는 일도 많은데 시간은 부족하고, 어떤 일부터
    시작해야 할지 막막해하고 고민하며, 때로는 차일피일 미루기만 하다 기한을
    놓치는 경우도 더러 있습니다.

-   앞서 시간 관리 문제들을 살펴보면 그 이유를 알 수 있습니다. 바로 시간 관리
    방법을 잘 모르기 때문입니다. 계획을 짜는 것은 귀찮고 시간이 오래 걸리는
    일이고 오히려 방해하는 것이라 생각합니다. (김영치, 2005) 구체적으로 어떻게
    짜야 할지는 모르겠고 해야 할 일은 쌓여만 갑니다.

-   가장 간단한 방법은 할 일을 생각나는 순서로 또는 일이 생길 때마다 어딘가에
    기록하는 것입니다. 그 아이디어를 구현한 것이 바로 할 일 리스트(To do list),
    체크리스트(check list)입니다. 이제 할 일 리스트를 통해 어떤 일들이 나를
    기다리고 있는지 알 수 있습니다.

-   그러나 여전히 어떤 일부터 시작해야 할지 고르는 것은 막막한 일입니다. 그래서
    추가된 기능이 기한(날짜)에 따른 정리입니다. 기존 체크리스트에서 메모들을
    날짜별로 정리하는 경우도 있고 아예 달력을 통째로 옮겨 놓고 일자별로 세세하게
    일정을 관리할 수 있는 캘린더(calendar)도 있습니다.

-   표 \<시간 관리 어플리케이션 비교\>에는 Google Play Store의 생산성 부문 상위
    어플리케이션 중 시간 관리와 관련된 6개 어플리케이션을 랭킹 순위대로 가져와
    비교한 것입니다.

-   컬러노트 메모장은 메모와 리스트 메모, 달력을 지원하는 어플리케이션입니다.
    10년 넘는 역사와 1억 다운로드 이상의 기록을 가지고 있으며 이용자층이
    탄탄합니다. 컬러별로 메모를 분류하는 것이 특징이고 특이 기능으로 암호 메모를
    제공합니다.

-   똑똑 노트는 메모와 장보기 목록, 생일 관리, 계좌번호 관리, 사이트 아이디 관리
    기능을 제공하는 생활 메모 특화 어플리케이션입니다. 특별한 기능은 포함되어
    있지 않지만 자주 사용하는 생활 편의 기능을 특화하여 많은 사람들이 사용하는
    것으로 분석됩니다.

#### 시간 관리 어플리케이션 비교
| 컬러노트 메모장 | 똑똑 노트 | Google 캘린더 | 잊지마 할일 | Microsoft To Do | To-do! |
|-------------|------------------|--------------|--------------|--------------|--------------------|
| 21위        | 22위             | 34위         | 64위         | 94위         | 102위              |
| 메모, 캘린더 | 메모, 체크리스트 | 캘린더       | 체크리스트   | 체크리스트   | 체크리스트, 캘린더 |
| 날짜별 할 일 | X                | 날짜별 할 일 | X            | 반복적 할 일 | 날짜별 할 일       |
| X           | X                | 오늘 할 일   | X            | 오늘 할 일   | 오늘 할 일         |
| 위젯, 알림   | 위젯             | 위젯, 알림   | 잠금 화면    | 위젯         | 위젯               |
| 커스터마이징 | X                | 커스터마이징 | 커스터마이징 | 커스터마이징 | X                  |
| 동기화       | X (백업 지원)    | 동기화       | X            | 동기화       | X                  |
>   (Google, 2020; Notes, 2020; Cleveni, 2020; MINUS to ZERO, 2020; Microsoft,
>   2020; 1:3 Lab, 2019)

-   Google 캘린더는 날짜별 시간별로 세세하게 일정을 관리할 수 있습니다. 특이
    기능으로 Gmail에 포함된 일정(항공편, 호텔 등)을 자동으로 추가해주고 자동
    동기화로 웹에서도 일정을 확인할 수 있습니다.

-   잊지마 할일은 매우 간단한 인터페이스에 할 일 추가, 삭제, 공유, 위치 조정만
    지원합니다. 잠금을 해제할 때마다 할 일 목록을 표시하여 할 일을 계속
    상기시키는 점이 특징입니다.

-   Microsoft To Do는 어느 환경에서나 저장한 할 일 목록을 가져올 수 있고 앱
    사용자간 목록을 공유할 수 있습니다. 컬러 노트처럼 색 별로 구분된 목록을
    만들고 반복 기한 설정이 가능합니다. 특별한 기능은 작업 나누기와 작업별 파일
    첨부 기능입니다.

-   To-do!는 메모와 오늘 할 일 날짜 별 할 일을 저장하는 매우 단순한
    어플리케이션입니다. 여타 비슷한 어플리케이션과 비교해서 특별한 서비스는
    없지만 간단하고 편리한 접근성을 무기로 사용자들을 끌어들이고 있습니다.

-   시간 관리 어플리케이션을 비교한 결과는 아래와 같습니다.

1.  복잡하고 다양한 기능을 제공하는 어플리케이션 뿐만 아니라 간단해서 사용하기
    용이한 어플리케이션, 특정 기능에 특화된 어플리케이션도 인기있었습니다.

2.  사용자는 어플리케이션에 중요한 할 일들을 등록하기 때문에 동기화를 지원하는
    편이 좋습니다. 실제로 동기화를 지원하지 않는 어플리케이션(똑똑 노트, 잊지마
    할일, To-do!)의 리뷰에 데이터 삭제 및 복구 관련 낮은 평가가 많았습니다.

3.  대부분의 어플리케이션이 위젯 또는 알림 기능을 지원하여 앱에 들어가지 않고도
    홈 화면에서 할 일 목록을 볼 수 있도록 설계하였습니다.

4.  관련성 있는 항목은 색깔 별로 정리하는 형태(커스터마이징)를 사용하는 경우가
    많았습니다.

-   그러나 기존의 시간 관리 어플리케이션에서는 단순히 일정들의 기한만 표시할 뿐
    각 일에 대한 중요도와 긴급도를 고려하지 않기 때문에 일의 우선 순위를
    파악하는데 어려움이 있습니다. 또한 시간 관리 방법을 제시하지 않아 시간
    관리의 필요성은 느끼지만 효율적인 시간 관리 방법을 모르는 사용자들은 시간
    계획을 세우는 것을 기피하게 되고 어려워할 수 있습니다. (김영치, 2005) 그래서
    일의 우선 순위를 제공하여 시간 낭비를 줄이고 사용자들의 시간 관리 부담을
    줄이려고 합니다.

-   이는 기존의 캘린더 어플리케이션과 같이 꽉 찬 일정을 시간 관리의 척도로
    여겼던 관점에서 벗어나 자신의 목표와 일의 중요도, 긴급도를 고려한 시간
    활용의 효율성을 고려한 시간 관리(오지현, 2008)로 인식을 변화시킬 수
    있습니다.

-   기존 어플리케이션에서 컬러 노트와 Microsoft To Do의 색을 사용한 일 분류,
    잊지마 할일과 To-do!의 간단함, 사용 편의성 등을 차용하여 여타 프로그램에서
    제공하지 않는 중요도 기반의 우선순위 정렬 기능을 추가할 것입니다.

## 문제 해결 방법 (알고리즘 및 아이디어)

### 1.  아이디어 소개

| 1 | 긴급하고 중요한 일            | 해라     |
|---|-------------------------------|----------|
| 2 | 긴급하지 않으나 중요한 일     | 계획하라 |
| 3 | 긴급하나 중요하지 않은 일     | 위임하라 |
| 4 | 긴급하지도 중요하지도 않은 일 | 없애라   |

-   세계적으로 2500만부가 팔린 책, The 7 Habits of Highly Effective People에서
    저자 Covey는 효율적인 사람들의 7가지 습관을 소개합니다. 그 중 먼저 해야 할
    것을 먼저 하라(소중한 것을 먼저 하라, First things first)파트에서 중요도와
    긴급도에 따른 분류 방법을 소개하고 중요도-긴급도 행렬(matrix)을 제시합니다.
    (Covey Stephen, 1989)

-   이 행렬은 제작할 어플리케이션의 주요 개념이라고 할 수 있습니다. 각 일은
    중요도와 긴급도를 가지고 그 값에 따라 자동으로 우선순위가 정해질 것입니다.
    위의 분류 방법처럼 긴급하고 중요한 일을 최우선으로 하고 긴급하지 않으나
    중요한 일, 긴급하나 중요하지 않은 일 순서대로 표시하는 것은 현실적으로
    일치하지 않는 부분이 있다고 생각하였습니다. (긴급하나 중요하지 않은 일을
    사용자에게 알리지 않을 경우 사용자는 큰 낭패를 볼 수가 있기 때문입니다.)
    Convey처럼 긴급하나 중요하지 않은 일을 위임하기 위해서는 사용자의 할 일
    리스트에 표시되어야 하는 것이 바람직하다 판단하여 이 중요도-긴급도 행렬을
    일부 변형한 아이디어를 차용하였습니다.

-   마이크로소프트에서 마우스 오른쪽 클릭, 더블클릭, 드래그 앤 드롭 기능을
    개발하고 윈도우95, 98등을 설계한 나카지마 사토시의 책에는 "마감에 가까워질
    때 힘껏 속력을 높이는 행동을 라스트 스퍼트 지향성으로 정의하고 시간 관리를
    위해 가장 먼저 버려야할 악습이다. 일을 해낸다는 건 정확히 말하면 언제나
    마감을 지킬 수 있는 방식으로 일하는 것이다. 주어진 마감일보다 더 앞서
    자신만의 마감일을 정하라. 역설적이게도 마감일을 지키려고 하면 지킬 수 없게
    된다. 마감 당일을 골인 지점이라고 생각해서는 절대 안 된다."(나카지마 사토시,
    2017)는 부분이 있습니다. 이러한 그의 생각을 녹여 내기 위해 긴급도 계산에
    있어 다른 어플리케이션보다 보수적으로 계산하여 사용자에게 알리려 합니다.

### 2.  어플리케이션 작동 과정

### 3.  아이디어 주요 개념

-   어플리케이션에서 가장 중요한 부분은 일의 우선 순위를 결정하는 과정입니다.
    아래에는 우선 순위 결정 과정에 사용되는 주요 개념과 방법을 기술하겠습니다.

-   중요도는 중요, 보통, 중요하지 않음 3단계 또는 5단계 이내로 할 것입니다.
    단계가 너무 많아 사용자가 중요도를 선택하는 부분에 있어서 피로를 느끼고
    기피하게 되는 것을 방지하기 위함입니다.

-   긴급도는 일의 기한과 예상 소요 시간을 바탕으로 실시간으로 계산합니다. 기한과
    예상 시간에 딱 맞춰서 우선 순위를 정할 경우 예기치 못한 상황이나 추가 작업
    필요로 일을 제대로 마치지 못할 가능성이 있습니다. 또 일이 끝난 후 상대방에게
    전송하거나 알려야 하는 경우도 있습니다. 그러나 사용자는 일의 마무리에 필요한
    시간은 고려하지 않고 정해진 기한만을 생각하는 경우가 많습니다. 그래서 예상
    소요 시간은 입력한 시간보다 가산하고 기한은 입력한 시간보다 감산하여 수치를
    보정합니다.

-   최소 시작 기한은 일을 완전하게 끝낼 수 있는 최소한의 시간을 보장하는 기한을
    의미합니다. 일반적으로 보정된 예상 시간에서 보정된 기한을 뺀 값으로 구할 수
    있습니다. 이 최소 시작 기한에 가까워질수록 긴급도 값이 높아지게 됩니다.

-   예를 들어 사용자가 12시까지 예상 소요 시간이 4시간인 일을 입력하면
    내부적으로 예상 소요 시간은 6시간, 기한은 11시까지를 기준으로 합니다. 이때
    최소 시작 기한은 보정된 기한 11시에서 보정된 예상 소요 시간 6시간을 뺀 5시가
    됩니다. 이는 최소한 5시에는 이 일을 시작해야 여유 있게 일을 완전히 끝낼 수
    있다는 것을 의미합니다.

-   우선 순위는 중요도와 긴급도를 합해서 결정됩니다. 중요도는 사용자가 변경하는
    경우를 제외하고 변하지 않지만 긴급도는 시시각각 변하므로 우선 순위도 이에
    맞추어 변경됩니다.

-   우선 순위 결정 과정에서 사용자가 입력한 모든 일을 남은 시간 내에 수행하는
    것이 불가능하다고 판단될 경우가 있을 수 있습니다. 이런 경우 기본값으로
    중요도가 높고 기한내 수행 가능한 일을 우선적으로 선택합니다. 사용자에게는
    입력한 일 중 일부를 수행할 수 없다고 알립니다. 사용자는 어플리케이션이
    선택한 우선순위대로 일을 수행할지 사용자가 판단하기에 더 중요하다고 생각되는
    우선순위로 조정할지 선택할 수 있습니다. 어플리케이션은 사용자가 조정한
    우선순위에 맞게 적합한 이후 우선순위 일정을 제공합니다.

## 기대 효과

-   시간은 무한하지 않기 때문에 주어진 시간을 얼마나 알뜰하게 이용하는 지가
    중요합니다. 이 어플리케이션은 나에게 가장 필요하고 중요한 일을 처리할 수
    있도록 우선순위를 판단하여 제공합니다. 불필요한 시간 낭비를 줄이고
    효율적으로 수행할 수 있게 도와줍니다.

-   복잡한 현대 사회에서 현대인의 주요 고민은 시간 관리 문제입니다. 시간 관리로
    자신의 역할 혼미 지각을 줄이고 주변의 압박 등 스트레스에 따른 소진을
    예방하게 되고 더 나아가 자기효능감[^1]과 자아개념을 높여 삶에 긍정적인
    태도와 자신감을 갖게 합니다. (오지현, 2008)

    [^1]: 어떤 상황에서 적절한 행동을 할 수 있다는 기대와 신념

-   구체적으로 초, 중학생을 대상으로 한 연구에서 합리적인 시간 계획을 세우고
    도전 가능한 목록을 설정한 다음 일의 우선 순위에 따라 실천한 경우 전체적인
    자기효능감, 자기조절능력, 과제수행능력이 향상된 것으로 나타났으며 (오현진,
    정지윤, 2006; 김소영, 서봉금, 김정섭, 2014) 전반적인 시간관리능력 향상으로
    미래지향적인 시간관을 확립할 수 있게 되었습니다. (전중원, 김정섭, 2019) 또한
    고등학생을 대상으로 한 연구에서 목표 설정, 계획, 우선순위 세우기, 실행
    과정이 학업성취도에 영향을 주는 것으로 드러났으며 (김주리, 2012) 구체적으로
    시간 관리가 자기효능감, 사회적지지의 증가를 동반하는 것으로 나타났습니다.
    (심해원, 황은숙, 2013)

-   한편 직장인을 대상으로 진행된 시간관리만족도 조사에서는 시간만족도를 크게
    관리성과만족도, 시간배분만족도, 시간여유만족도로 나누어 그 경향을 조사한
    결과, 시간관리행동 중 계획성을 더 고려할수록 관리성과만족도,
    시간배분만족도가 높게 나타났으며 긴급성을 고려할수록 시간여유만족도는 낮게
    나타났습니다. 이는 계획적 생활을 동반하면 시간을 관리하면서 성과에 만족할 수
    있고 노동시간 단축으로 대인관계 증진 및 삶의 여유를 찾는데 기여할 수 있다고
    볼 수 있습니다. (채화영, 2005)

-   특히 시험과 같은 중요한 일을 앞두고 있을 때에는 시간 관리를 수행하지 않은
    쪽은 평소보다 학습된 무기력이 높게 자기효능감은 낮게 측정된 반면 시간 관리를
    수행한 쪽에서는 학습된 무기력은 낮게 자기효능감은 소폭 증가하는 경향을
    보였습니다. (주승열, 2015)

-   스마트폰 애플리케이션을 이용한 사례로는 시간관리 자기 규제를 위한 스마트폰
    애플리케이션 개발 및 효과 검증 연구가 있습니다. 연구에서는 사용자들의 시간
    관리를 통한 자기조절능력 관리 및 개선을 지원하고자 개발한 프로그램을
    설치하고 시간 관리 효과 및 자기 규제 가능성에 대해 조사하였습니다. 사용자는
    어플리케이션에서 목표 정하기, 할 일 조정, 자기 관찰과 반성의 과정을
    거쳤습니다. 사용자의 74%는 시간의 자기 관리 및 시간 규제에 있어 긍정적인
    결과를 보였습니다. 사용자들은 첫번째로 놀라움과 깨달음(surprise and
    awareness) 둘째로 선택 범위의 제한(restricted range of choice) 마지막으로
    주요 집중 방해(주의 산만)(main distraction) 요소 파악을 경험했다고
    밝혔습니다. (김보관, 한경식, 2019)

-   균형적인 삶 : 시간 관리를 잘하여 일하는 시간을 줄인다면, 삶에 여유가 생겨 일
    외에 자신의 다양한 여가를 즐길 수 있고 여러 사람들과 만나 대인관계가
    증진됩니다. 일상이 균형 잡힌 삶을 살 수 있도록 도와줍니다.

-   생산성 향상 : 향상된 자기 관리 능력과 시간 사용 규제로 불필요한 일과 시간을
    줄여 생산성을 높일 수 있습니다. 향상된 생산성은 노동 시간 감소로 이어지고
    이는 시간여유만족도의 증가로 나타납니다.

-   자신의 목표 달성 : 자신감, 긍정적 태도, 미래지향적인 생각은 목표에 보다
    가깝게 다가설 수 있도록 합니다. 장기 프로젝트는 기한을 잘게 쪼개어 나누고
    선택 범위를 제한하여 목표와 관련 적은 항목을 배제합니다. 집중적이고 효율적인
    시간 관리로 목표에 매진함으로써 계획에 맞추어 한발짝 목표에 다가설 수 있게
    됩니다.

-   스트레스 감소 : 현대인들의 가장 큰 문제인 시간 갈등, 시간 압박 같은 시간적
    스트레스를 해결하여 스트레스를 줄일 수 있습니다.

## 참고 문헌

김보관, 한경식 (2019). 시간관리 자기 규제를 위한 Automated Time Manager (ATM)
스마트폰 애플리케이션 개발 및 효과 검증. 한국HCI학회 학술대회, 1280-1281

김소영, 서봉금, 김정섭 (2014). 목표설정 중심의 시간관리 프로그램이 중학생의 진로
자기효능감에 미치는 효과. 사고개발, 10(2), 31-47

김영치 (2005). 시간 관리의 효율성과 효과성 : 낭비요인의 극복과 새로운 패러다임의
모색. 지역산업연구, 28(1), 5-33

김주리 (2012). 고등학생의 시간관리 능력이 시간관리 만족감과 학업성취도에 미치는
영향, 고려대학교 석사논문, 63-67

나카지마 사토시 (2017). 오늘 또 일을 미루고 말았다. 북클라우드.

심해원, 황은숙 (2013). 고등학생의 시간관리와 학업성취의 관계에서 자기효능감과
사회적지지의 매개효과. 청소년학연구, 20(11), 1-21

오지현 (2008). 시간관리 프로그램이 초등학생의 시간 관리에 미치는 효과.
제주대학교 교육대학원 석사논문, 6-31

오현진, 정지윤 (2006). 초등학생의 시간관리 능력에 따른 자기효능감 연구.
한국실과교육학회지, 19(3), 101-112

전중원, 김정섭 (2019). 시간관리 프로그램이 전환기 초등학생의 시간관리능력과
미래지향시간관에 미치는 효과. 사고개발, 15(1), 43-59

주승열 (2015). 시간관리 동기부여 프로그램이 학습된 무기력, 자기효능감,
학습동기에 미치는 영향. 고려대학교 석사논문, 42-43

채화영 (2005) 직장인의 시간관리행동과 시간관리만족도 연구,
한국가족자원경영학회지, 9:3, 31-43

Covey R. Stephen. (1989). The 7 Habits of Highly Effective People.

Google (2020). Google Play Store. https://play.google.com/store.

# 중간 보고서 (팀 13)

우선 순위 기반 시간 관리 어플리케이션 개발

## 1. 주제

현대인들의 주요 고민(오현진, 정지윤, 2006)인 시간 관리 문제를 돕는
어플리케이션을 설계하고 제작한다. 사람들은 시간 관리의 필요성에 대해서는
인식하고 있지만(김주리, 2012) 시간 관리 개념과 방법에 대한 인식이 부족하여 시간
관리를 어려워한다.(김영치, 2005) 시간 관리 중요성에 대한 인식도 변화하고 있는데
기존의 꽉 찬 일정의 나열을 시간 관리 척도로 여기던 것에서 자신의 목표 일의
중요도, 긴급도를 고려한 시간 활용으로 전환되고 있다.(오지현, 2008)

기존 어플리케이션들은 이런 사용자의 요구에 대항하지 못하고 있다. 뚜렷한 시간
관리 방법을 제시하거나 변화하는 인식에 발 맞춘 서비스를 기대하기 힘들다. 기존
어플리케이션에서 장점으로 여겨졌던 색을 통한 분류, 동기화 기능은 그대로 가져오고
Covey가 소개한 중요도-긴급도 행렬(아이젠하워 행렬)을 이용한 방법(Covey Stephan,
1989)과 이를 일부 차용한 알고리즘을 이용하여 사용자들에 시간 관리 방법을
제시한다. 또한 Satoshi의 할 일 쪼개기, 기한과 소요 시간 여유롭게
고려하기(Satoshi Nakajima, 2016)를 통해 바른 시간 관리 습관을 가지도록 유도하고
사용자의 체계적인 목표(할 일) 수행을 돕는다.

여러 연구 결과를 통해 이미 시간 계획이 스트레스 감소, 무기력함 감소, 자기효능감
향상, 자기조절능력 향상, 과제(업무, 학업)수행능력 향상, 시간 자기 규제 능력
향상, 대인 관계 증진, 자신감 증가 같은 효과를 준다는 것이 입증된 바
있다.(오현진, 정지윤, 2006; 김소영, 서봉금, 김정섭, 2014; 오지현, 2008; 전중원,
김정섭, 2019; 김주리, 2012; 심해원, 황은숙, 2013; 주승열, 2015; 김보관, 한경식,
2019; 채화영, 2005) 어플리케이션을 통해 손쉽게 나에게 중요하고 필요한 일을
파악할 수 있고 일의 순서를 계획하는데 사용되는 불필요한 시간 낭비를 줄여 바로
실행에 돌입할 수 있게 된다.

## 2. 구현 목표

### 할 일 우선 순위

-   사용자는 할 일을 추가하기 위해 프로젝트를 선택하고 이름, 중요도, 기한, 예상
    소요 시간 등을 입력한다 (프로젝트는 예상 소요 기간을 지원하지 않음).

-   사용자가 입력한 정보를 바탕으로 중요도, 긴급도를 계산한다.

-   계산한 중요도, 긴급도로 우선 순위를 판단, 사용자에 할 일 목록을 제공한다.

-   목록은 간단한 카드 형태로 제공되며 오른쪽의 자세히 보기(카드 확장) 버튼을
    눌러 상세 정보 조회가 가능하다.

-   목록은 범위를 선택하여 전체 또는 각 프로젝트별 우선 순위를 받아볼 수 있으며
    중요도, 기한에 따른 정렬 기능도 지원한다.

### 프로젝트별 분류

-   각 할 일들은 프로젝트 단위로 분리되며 각 프로젝트는 프로젝트 고유색을
    가진다.

-   사용자가 프로젝트 달성을 위해 할 일을 세부적으로 분리하고 각 할 일별로
    중요도, 기한, 예상 소요 시간을 작성하도록 유도해 바른 시간 관리 습관을
    들이고 효율적, 체계적인 시간 관리 기법을 제공하여 사용자가 목표를 달성할 수
    있도록 돕는다.

-   프로젝트 별로 할 일을 추가하도록 하여 우선 순위, 행렬, 분석 등 다른 기능에서
    프로젝트별 조회가 가능하다.

### 중요도 – 긴급도 행렬 시각화

-   사용자가 실시간으로 중요도 – 긴급도 행렬을 볼 수 있도록 하여 리스트로 제공될
    때와는 다르게 각 프로젝트와 할 일의 분포와 규모를 한 눈에 파악할 수 있다.

-   행렬은 중요하고 긴급한 일(1), 중요하지만 긴급하지 않은 일(2), 긴급하지만
    중요하지 않은 일(3), 중요하지도 않고 긴급하지도 않은 일(4)로 나뉜다.

-   사용자는 각 사분면을 눌러서 확대된 행렬을 볼 수 있다. 이 확대된 행렬
    보기에서는 사용자가 할 일을 누르면 할 일의 세부정보가 표시된다.

-   기존 중요도 – 긴급도 행렬에서 중요하고 긴급하지 않은 일과 긴급하고 중요하지
    않을 일을 직접 확인하여 둘 사이의 모호한 우선 순위 관계를 사용자가 직접
    살펴볼 수 있다.

-   다른 프로젝트는 색이 다르게 표시되어 사용자는 각 프로젝트별 분포를 손쉽게
    파악 가능하다.

-   행렬 아래에는 할 일 목록을 나열, 할 일 우선 순위 화면(activity)에서와 같이
    제공하여 사용자가 바로 해당 행렬에 포함된 할 일들을 볼 수 있다.

### 목표 달성 정도 분석

-   할 일을 완료하면 사용자는 할 일 목록에서 완료를 선택한다.

-   이미 완료한 일은 우선 순위 목록에서는 사라지지만 사용자는 해왔던 일을
    확인해야할 경우가 있을 수 있다. 전체 또는 프로젝트 별로 완료한 일과 해야 할
    일을 모두 표시하여 전체적인 진행 현황을 살필 수 있다.

-   성공, 실패, 진행 중 비율과 각 항목 수를 제공하여 진행중인 프로젝트의 경우
    목표 달성을 위한 대략적인 수행 정도를 파악할 수 있고 완료한 프로젝트의 경우
    전체적인 수행 경과 파악과 달성, 실패 비율을 통한 자기 반성 및 성찰 가능하다.

### 프로젝트, 할 일 목록 동기화

-   사용자가 입력한 프로젝트, 할 일은 기기의 데이터베이스에 저장된다.

-   아이디, 비밀번호를 통한 로그인으로 동기화 서버에 데이터베이스 저장을
    지원한다.

-   사용자가 인터넷에 연결되어 있는 경우 기기의 데이터베이스와 서버의
    데이터베이스 모두에 저장하고 연결되어 있지 않은 경우 기기의 데이터베이스에만
    저장하고 향후 인터넷에 연결될 경우 서버 데이터베이스에 동기화한다.

-   모종의 이유로 어플리케이션의 데이터가 삭제되었을 경우 사용자는 로그인하여
    서버에 저장된 데이터로 복구할 수 있다.

## 3. 개발 환경

### 안드로이드 어플리케이션

-   개발 플랫폼 : Android 7.0 (Nougat) 이상

-   개발 도구 : Android Studio (IntelliJ IDEA), GIMP, Inkscape

-   개발 언어 : Kotlin, Java, XML 등

-   데이터베이스 : SQLite

-   지원 언어 : 한국어, English

### 동기화 서버

-   개발 플랫폼 : Ubuntu 18.04.4 LTS

-   개발 도구 : 구름 IDE, Atom, Chrome

-   개발 언어 : JavaScript (Node.js, Express4), HTML 등

-   데이터베이스 : MySQL

-   지원 언어 : 한국어

## 4. 주요 개발 일정
| 이름                               | 기간             | 상태            | 비고 |
|------------------------------------|------------------|-----------------|------|
| 제안서 작성 및 발표                | 2020/4/8 - 4/12  | 완료(2020/4/12) | 수정 |
| 안드로이드(Kotlin(JVM), XML) 학습  | 2020/4/10 – 4/26 | 완료(2020/4/26) |      |
| 주요 서비스 제공 항목 검토 및 확정 | 2020/4/13 - 4/26 | 완료(2020/4/24) |      |
| 제안서 작성 및 발표(수정)          | 2020/4/27 – 5/1  | 완료(2020/5/1)  |      |
| 데이터베이스(SQL) 학습             | 2020/4/27 – 5/3  | 완료(2020/5/2)  |      |
| 개발 환경 및 서비스 환경 구상      | 2020/4/27 – 5/15 | 완료(2020/5/13) |      |
| 서버(Node.js, Express4) 학습       | 2020/5/4 – 5/24  | 진행            |      |
| 안드로이드 어플리케이션 설계       | 2020/5/8 – 5/17  | 완료(2020/5/13) |      |
| 중간 보고서 작성 및 발표           | 2020/5/15 – 5/22 | 완료(2020/5/22) |      |
| 안드로이드 어플리케이션 제작       | 2020/5/18 – 6/7  | 진행            |      |
| 동기화 서버 설계                   | 2020/5/25 – 6/7  | 예정            |      |
| 동기화 서버 제작                   | 2020/6/8 – 6/21  | 예정            |      |
| 결과 보고서 작성                   | 2020/6/22 – 6/26 | 예정            |      |
| 최종 점검                          | 2020/6/22 – 6/28 | 예정            |      |
| 최종 발표 및 시연                  | 2020/6/29 – 6/30 | 예정            |      |

> 2020/5/22 기준

## 5.  개발 현황

### 안드로이드 어플리케이션 레이아웃

구체적인 안드로이드 어플리케이션 주요 서비스 제공 항목은 할 일, 프로젝트, 행렬,
분석 4가지 항목이다.

할 일에서는 할 일을 추가, 완료, 수정, 삭제할 수 있고 할 일 목록이 우선 순위에
따라 표시된다. 이 때 할 일을 추가하기 전에 프로젝트를 선택해야 하고 사용자가
새로운 프로젝트 생성을 원할 경우 프로젝트 추가 과정으로 이동될 수 있다.

프로젝트에서는 프로젝트 추가, 완료, 수정, 삭제를 할 수 있다. 기본적으로 일상
프로젝트를 제공하고 사용자가 만든 프로젝트들이 목록으로 표시된다.

행렬에서는 중요도와 긴급도에 따른 중요도-긴급도 행렬(아이젠하워 행렬)을 볼 수
있다. 사용자는 각 사분면을 고르면 확대된 행렬을 볼 수 있고 각 행렬별로 세부적인
할 일 목록을 받아볼 수 있다.

분석에서는 프로젝트별로 완료, 실패, 수행 중인 일들의 비율을 볼 수 있고 이전에
완료한 일들의 수행 목록을 확인할 수 있다.

설정에서는 어플리케이션 관련 환경 설정 항목을 제공한다. 사용자는 동기화를 위한
옵션과 알림을 위한 옵션을 선택할 수 있다.

### 안드로이드 어플리케이션 아이콘 및 벡터 이미지 제작

오픈소스 이미지 수정 도구인 GIMP와 벡터 이미지 수정이 가능한 Inkscape를
이용한다. 어플리케이션 아이콘은 PNG 형태로 제작하여 사용해야 하므로 GIMP를
사용하고 어플리케이션 내에서 사용되는 벡터 에셋(Vector Asset) 중 클립아트에 없는
벡터 파일(SVG) 제작을 위해 서는 Inkscape를 사용한다. 안드로이드 특성상 매우
다양한 종류의 안드로이드 디바이스가 존재하고 각 디바이스마다 화면 크기가 다르다.
각 디바이스에 맞게 확대, 축소될 수 있는데 벡터 이미지를 사용하면 확대, 축소되는
과정에서 이미지가 깨지지 않고 원래의 형태가 유지된다.

Google play 스토어에서는 별도의 아이콘 디자인 사양을 규정하고 있다. 어플리케이션
아이콘은 정사각형 512px, 32비트, 1MB 이하, PNG 파일을 사용해야 하며 제품 아이콘
키라인을 준수해야 한다.(Google, 2020)

시간 관리 어플리케이션인 만큼 시계와 체크 표시를 활용하여 어플리케이션 아이콘을
디자인하려 한다. 전체적인 형태는 시침과 분침을 이용해 체크 표시를 만들고
중앙에는 작은 원을 넣은 형태로 구성할 예정이다.

### 안드로이드 어플리케이션 데이터베이스

프로젝트는 projects 테이블에 할 일을 tasks 테이블에 저장한다. primary key는 id로
하고 각 할 일들은 project_id를 저장하여 프로젝트를 구분한다. name, importance,
deadline, estimated_time, memo에 사용자가 입력한 할 일 이름, 중요도, 기한, 예상
소요 시간, 메모를 저장한다. status는 할 일, 프로젝트의 완료 여부를 저장하며
complete_date는 완료 날짜를 저장한다. 프로젝트의 success, fail, in_progress에는
해당(성공, 실패, 진행중)되는 할 일의 수를 구분하여 저장한다. 각 프로젝트에
해당되는 일들을 조회하여 수를 알아낼 수 있겠지만 분석 기능 제공 중 빠른 접근을
위해 항목들을 별도 저장한다. sync는 동기화해야 할 항목인지 여부를 저장한다.
동기화시 새롭게 추가하거나 수정한 항목, 삭제한 항목을 체크하여 인터넷에 연결되면
해당되는 항목을 서버의 데이터베이스에 동기화하기 위함이다.

### 서버 개발 및 서비스 환경

구름IDE 서비스 이용하여 메모리 1GB, 저장공간 10GB 컨테이너를 대여하여 서버
환경을 조성한다. Ubuntu 18.04 LTS 운영체제에 서버 작동을 위해 Node 10.16.3,
Express4 4.15.5를 사용하고 데이터베이스 관리를 위해 MySQL을 사용한다. 개인
컴퓨터를 서버로 사용하면 여러 디바이스에서 개발하는데 문제가 있을 수 있다고
판단하여 외부 클라우드 환경을 사용하기로 결정하였다.

서버 관리 패키지로는 pm2를 사용하고 이외 cookie-parser, body-parser, helmet,
compression, passport등의 패키지를 사용하여 서버를 구현할 예정이다.

서버 주소 : <https://todo-server.run.goorm.io/>

### 서버 데이터베이스

안드로이드 어플리케이션과 동일한 데이터베이스 테이블을 사용한다. 향후 Web 서비스
지원을 하게 되는 것을 염두에 두고 sync항목을 유지하여 안드로이드 어플리케이션의
변경 사항과 Web에서의 변경 사항을 병합할 때 사용하려 한다.

안드로이드 어플리케이션과 서버는 JSON 형태로 데이터를 주고받고 안드로이드
어플리케이션에서는 JSON 데이터를 파싱하여 사용한다.

## 참고 자료


김보관, 한경식 (2019). 시간관리 자기 규제를 위한 Automated Time Manager (ATM)
스마트폰 애플리케이션 개발 및 효과 검증. 한국HCI학회 학술대회, 1280-1281

김소영, 서봉금, 김정섭 (2014). 목표설정 중심의 시간관리 프로그램이 중학생의 진로
자기효능감에 미치는 효과. 사고개발, 10(2), 31-47

김영치 (2005). 시간 관리의 효율성과 효과성 : 낭비요인의 극복과 새로운 패러다임의
모색. 지역산업연구, 28(1), 5-33

김주리 (2012). 고등학생의 시간관리 능력이 시간관리 만족감과 학업성취도에 미치는
영향, 고려대학교 석사논문, 63-67

심해원, 황은숙 (2013). 고등학생의 시간관리와 학업성취의 관계에서 자기효능감과
사회적지지의 매개효과. 청소년학연구, 20(11), 1-21

오지현 (2008). 시간관리 프로그램이 초등학생의 시간 관리에 미치는 효과.
제주대학교 교육대학원 석사논문, 6-31

오현진, 정지윤 (2006). 초등학생의 시간관리 능력에 따른 자기효능감 연구.
한국실과교육학회지, 19(3), 101-112

전중원, 김정섭 (2019). 시간관리 프로그램이 전환기 초등학생의 시간관리능력과
미래지향시간관에 미치는 효과. 사고개발, 15(1), 43-59

주승열 (2015). 시간관리 동기부여 프로그램이 학습된 무기력, 자기효능감,
학습동기에 미치는 영향. 고려대학교 석사논문, 42-43

채화영 (2005) 직장인의 시간관리행동과 시간관리만족도 연구,
한국가족자원경영학회지, 9:3, 31-43

Covey R. Stephen. (1989). The 7 Habits of Highly Effective People.

Google (2020). Google Developers. https://developer.android.com/.

Satoshi Nakajima (2016). なぜ,あなたの仕事は終わらないのか 文響社