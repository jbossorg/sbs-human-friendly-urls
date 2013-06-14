<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <#assign title = action.getText("plugin.hfurl.navigation.name") />
    <title>${title}</title>
    <meta name="nosidebar" content="true" />

    <content tag="breadcrumb">
    </content>
</head>
<body class="jive-body-formpage">

<!-- BEGIN header & intro  -->
<div id="jive-body-intro">
    <div id="jive-body-intro-content">
        <h1>${title}</h1>
    </div>
</div>
<!-- END header & intro -->
<!-- BEGIN main body -->
<div id="jive-body-main">

    <!-- BEGIN main body column -->
    <div id="jive-body-maincol-container">
        <div id="jive-body-maincol">

            <p><@s.text name="plugin.hfurl.navigation.description" /></p>
            <ul>
            <#list documents as doc>
              <li>
                <#-- 
                  Beware - when it's enabled HF URL links then s.url tag with "/docs/..." value modify to HF URL back.
                  We need to avoid HF URL link now
                 -->
                <a href="<@s.url value='/' includeParams='none' />docs/${doc.documentID}<#if pdfRequired?exists && "true" == pdfRequired?trim>.pdf</#if><#if urlSuffix?exists && "" != urlSuffix?trim>/${urlSuffix}</#if><#if decorator?exists>?decorator=${decorator}</#if>">
                <#list doc.communities as comm>
                  ${comm.name} &gt;
                </#list>
                ${doc.subject}
                </a>
              </li>
            </#list>
            </ul>
    
        </div>
    </div>
    <!-- END main body column -->


</div>
<!-- END main body -->
</body>
</html>
