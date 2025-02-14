<html>
<head>
    <meta charset="UTF-8"/>
    <title>전자위임장</title>
    <link rel="stylesheet" type="text/css" href="styles.css"/>
</head>
<body>
<div>
    <div class="top-div">
        <div class="top-div-left">(전자위임장)</div>
        <div class="top-div-right">${digitalDocumentNo}</div>
    </div>
    <div class="empty"></div>
    <div class="title" style="letter-spacing: 12px">위임장</div>
    <div class="margin-bottom-16px">
        본인은 ${shareHolderMeeting.date.year}년 ${shareHolderMeeting.date.month}월 ${shareHolderMeeting.date.day}일에 개최되는 ${companyName}의
        ${shareHolderMeeting.name}(그 속회, 연회 포함)에서 「자본시장과
        금융투자업에 관한 법률」 제 152 조 등에 따른 의결권 대리행사를 위하여
        권유자 ${acceptor.name}
        (<#if acceptor.corporateNo??>${acceptor.corporateNo}<#else>${acceptor.birthDate}-${acceptor.firstNumberOfIdentification}******</#if>, 이하
        “권유자”) 및/또는 권유자가 지정하는 자<#if designatedAgentNames??>(${designatedAgentNames})</#if> 중 1 인을 그 대리인으로 정하고 다음의 의사표시에
        따라 의결권을 행사할 것을 위임합니다. 한편, 본 위임장을 통한 의결권 대리행사는 단순히
        권유자를 통한 본인의 의결권 대리행사에 불과하며, 본인과 권유자와의 의결권 공동 행사 행위에 해당하지 않습니다.
    </div>
    <div class="next-label">-다음-</div>
    <div class="margin-bottom-16px">
        <div>1. 의결권 위임 내용</div>
        <table>
            <tr>
                <td class="section-1-header-td">주주번호</td>
                <td class="section-1-header-td">소유주식수</td>
                <td class="section-1-header-td">의결권 있는 주식 수</td>
                <td class="section-1-header-td">위임할 주식 수 전부</td>
            </tr>
        </table>
    </div>
    <div class="margin-bottom-16px">
        <div>2. 주주총회 목적사항 및 목적사항별 찬반 여부</div>
        <table style="width:100%">
            <colgroup>
                <col style="width:18%"/>
                <col style="width:58%"/>
                <col style="width:8%"/>
                <col style="width:8%"/>
                <col style="width:8%"/>
            </colgroup>
            <thead>
            <tr>
                <td class="section-2-header-td">구분</td>
                <td class="section-2-header-td">주주총회 목적사항</td>
                <td class="section-2-header-td">찬성</td>
                <td class="section-2-header-td">반대</td>
                <td class="section-2-header-td">기권</td>
            </tr>
            </thead>
            <tbody>
            <#list digitalDocumentItems>
                <#items as item>
                    <tr>
                        <#if !item.isLastItem>
                            <td class="bold-td">${item.title}</td>
                            <td class="bold-td" style="text-align: left">${item.content}</td>
                            <td class="bold-td" colspan="3"></td>
                        <#else>
                            <td>${item.title}</td>
                            <td style="text-align: left">${item.content}</td>
                            <#if item.selectValue??>
                            <td><#if item.selectValue = "APPROVAL">●</#if></td>
                            <td><#if item.selectValue = "REJECTION">●</#if></td>
                            <td><#if item.selectValue = "ABSTENTION">●</#if></td>
                            <#else>
                            <td></td>
                            <td></td>
                            <td></td>
                            </#if>
                        </#if>
                    </tr>
                </#items>
            </#list>
            </tbody>
        </table>
    </div>
    <#if version = "V2">
        <div class="margin-bottom-16px">
            <div>3. 새로 상정된 안건이나 변경ㆍ수정 안건 등에 대한 의결권의 행사위임</div>
            <div>- 주주총회시 새로이 상정된 안건이나 각호 의안에 대한 수정안이 상정될 경우에는 대리인은 해당 의안에 대해 기권으로 처리합니다.</div>
            <div>- 다만 아래의 명시적으로 지시한 사항에 대해서는 주주가 주주총회 전까지 별도의 의사표시가 없는 한 아래의 지시한 대로 의결권을 행사하겠습니다.</div>
            <div>
                <table style="width:100%">
                    <colgroup>
                        <col style="width:30%"/>
                        <col style="width:70%"/>
                    </colgroup>
                    <thead>
                    <tr>
                        <td class="section-2-header-td">항목</td>
                        <td class="section-2-header-td">지시내용</td>
                    </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td class="bold-td">&nbsp;</td>
                            <td class="bold-td">&nbsp;</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="margin-bottom-16px">
            <div>4. 이 위임장 작성 이전의 의결권 위임은 이 위임장으로 철회합니다.</div>
        </div>
    <#else>
    <div class="margin-bottom-16px">
        <div>3. 새로 상정된 안건이나 변경ㆍ수정 안건 등에 대한 의결권의 행사위임</div>
        <div>- 주주총회시 새로이 상정된 안건이나 각호 의안에 대한 수정안이 상정될 경우에는 대리인이 주주의 의사표시가 위 2번 항목에서 표시된 찬반의 취지에 합치된다고 합리적으로 판단되는 바에 따라 의결권을 행사할 것을 위임합니다.</div>
    </div>
    </#if>
    <div class="user-info-section digital-proxy-user-info-section-height">
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