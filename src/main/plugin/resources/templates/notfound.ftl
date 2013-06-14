<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <#assign title = action.getText("plugin.hfurl.notfound.name") />
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
            <p><@s.text name="plugin.hfurl.notfound.description" /></p>
            <p>
              <a href="javascript:history.go(-1)"><@s.text name="plugin.hfurl.notfound.link.back"/></a><br>
              <a href="<@s.url value='/community/wiki'/>"><@s.text name="plugin.hfurl.notfound.link.wiki"/></a>
            </p>
        </div>
    </div>
    <!-- END main body column -->


</div>
<!-- END main body -->
</body>
</html>