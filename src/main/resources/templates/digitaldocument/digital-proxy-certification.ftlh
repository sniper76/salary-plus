<html>
<head>
    <meta charset="UTF-8"/>
    <title>전자서명 본인 인증서</title>
    <link rel="stylesheet" type="text/css" href="styles.css"/>
</head>
<body>
<div>
    <h2>전자서명 본인 인증서</h2>
    <table class="document-info-table">
        <tr>
            <th class="document-info-title" colspan="2">전자문서 기본정보</th>
        </tr>
        <tbody>
        <tr>
            <td class="document-info-title">문서이름</td>
            <td>${companyName} ${shareHolderMeeting.date.month}/${shareHolderMeeting.date.day} <#if shareHolderMeeting.regularShareHolderMeeting>정기주주총회<#else>임시주주총회</#if> 전자위임장</td>
        </tr>
        <tr>
            <td class="document-info-title">문서번호</td>
            <td>${documentNo}</td>
        </tr>
        <tr>
            <td class="document-info-title">서명정보 상태</td>
            <td>완료</td>
        </tr>
        <tr>
            <td class="document-info-title">기준 시간</td>
            <td>(UTC+09:00) 한국 표준시</td>
        </tr>
        <tr>
            <td class="document-info-title">문서 페이지 수</td>
            <td>${originalPageCount + attachmentPageCount}P (본문 ${originalPageCount}P + 첨부자료 ${attachmentPageCount}P)</td>
        </tr>
        </tbody>
    </table>
    <h2>위임인 정보</h2>
    <table class="document-info-table">
        <tbody>
        <tr>
            <th class="document-info-title" colspan="2">위임인</th>
        </tr>
        <tr>
            <td class="document-info-title">이름</td>
            <td>${acceptor.name}(${acceptor.birthDate})</td>
        </tr>
        <tr>
            <td class="document-info-title">진행 정보</td>
            <td>서명 완료</td>
        </tr>
        <tr>
            <td class="document-info-title">본인 인증 정보</td>
            <td>휴대폰 본인 인증 완료, PIN 인증 완료</td>
        </tr>
        </tbody>
    </table>
    <h2>수임인 정보</h2>
    <table class="document-info-table">
        <tbody>
        <tr>
            <th class="document-info-title" colspan="2">수임인</th>
        </tr>
        <tr>
            <td class="document-info-title">이름</td>
            <td>${grantor.name}(${grantor.birthDate})</td>
        </tr>
        </tbody>
    </table>
    <h2>진행 이력</h2>
    <table class="document-info-table">
        <thead>
        <tr>
            <th>진행 시점 및 IP 주소</th>
            <th>진행 내용</th>
        </tr>
        </thead>
        <tbody>
            <#list progressRecords>
                <#items as item>
                    <tr>
                        <td class="document-info-title">${item.time}<br>${item.ipAddress}</td>
                        <td>${item.message}</td>
                    </tr>
                </#items>
            </#list>
        </tbody>
    </table>
</div>
<div class="footer">
    <div style="margin:32px 0px">전자문서 인증서_${acceptor.name}(${acceptor.birthDate})</div>
</div>
</body>
</html>
