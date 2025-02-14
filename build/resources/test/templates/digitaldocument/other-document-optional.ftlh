<html>
<head>
    <meta charset="UTF-8"/>
    <title>기타문서</title>
    <link rel="stylesheet" type="text/css" href="styles.css"/>
</head>
<body>
<div>
    <div>(전자문서)</div>
    <div class="title">${title}</div>
    <div class="margin-bottom-16px">
        <@content?interpret />
    </div>
    <div class="user-info-section">
        <div class="user-info-label">보유주식:&#160; &#160;</div>
        <div class="user-info-value">${companyName} / ${grantor.holdingStockQuantity}주 (보통주)</div>
        <div>
            <div class="user-info-label">주주명:&#160; &#160;</div>
            <div class="user-info-value signature-container">
                ${grantor.name}
                <#if signatureImageSrc??>
                <img class="signature" src="${signatureImageSrc}" alt="signature"/>
                </#if>
                <div class="signature-box">( 인 )</div>
            </div>
        </div>
        <div class="user-info-label">생년월일:&#160; &#160;</div>
        <div class="user-info-value">${grantor.birthDate} - ${grantor.firstNumberOfIdentification}******</div>
        <div class="user-info-label">일시:&#160; &#160;</div>
        <div class="user-info-value margin-bottom-16px">${grantor.signingDate.year} 년 &#160;${grantor.signingDate.month} 월
            &#160;${grantor.signingDate.day} 일
            &#160;${grantor.signingDate.hour} 시
        </div>
    </div>
    <div class="footer">
        <div style="margin-bottom: 8px">본 전자문서는 주식회사 컨두잇이 제공하는 전자문서작성 서비스 및 본인 확인 서비스를 통하여 주주 본인이 「전자서명법」에 따른 전자서명하여 작성한 문서이며,&#160; 행위일시 및
            IP 주소가 모두
            확인되었습니다.</div>
        <#if attachingFilesDescription??><div>첨부서류: ${attachingFilesDescription}</div></#if>
    </div>
</div>
</body>
</html>
