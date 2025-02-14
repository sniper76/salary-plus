<html>
<head>
    <meta charset="UTF-8"/>
    <title>공동보유확인서</title>
    <link rel="stylesheet" type="text/css" href="styles.css"/>
</head>
<body>
<div>
    <div>(전자확인서)</div>
    <div class="title">공동보유확인서</div>
    <div class="margin-bottom-16px">
        본인은 ${companyName}(법인등록번호: ${companyRegistrationNumber}, 이하 “회사”)이/가 발행한 보통주를 보유 하는 주주로서, 경영권 영향 목적으로 ${acceptor.name}(생년월일:
        ${acceptor
        .birthDate}, 이하
        “주주대표”)와/과
        의결권을 공동으로 행사하는 방식으로 해당 주식을
        다음과 같이 공동보유하고자 합니다. 본인은 추후 회사가 개최하는 주주총회(정기주주총회 및 임시주주총회를 포함하며, 그 속회/연회 포함)에서 「자본시장과 금융투자업에 관한 법률」 제 152 조 등에 따른 주주대표의 의결권 대리행사권유에 응하여
        주주대표 및/또는 주주대표가 지정하는 자 중 1인을 그 대리인으로 정하고 의결권을 위임할 의향이 있음을 확인합니다.
    </div>
    <div class="next-label">-다음-</div>
    <div class="margin-bottom-16px">
        <div class="section-title">1. 공동보유의 내용</div>
        <div>${content}</div>
    </div>
    <div class="margin-bottom-16px">
        <div class="section-title">2. 「자본시장과 금융투자업에 관한 법률」 제 147 조 상 대량보유보고의 위임</div>
        <div>- 본인은 「자본시장과 금융투자업에 관한 법률 시행령」 제153조 제4항에 따라 주주대표를 대표자로 하여 연명으로 대량보유보고함에 동의하며, 대량보유보고를 위하여 필요한 기재 정보가 올바로 기재되어 있음을 확인합니다.</div>
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
