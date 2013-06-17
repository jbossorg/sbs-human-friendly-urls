<!DOCTYPE html>
<html>
<head>
<#assign title = action.getText("plugin.hfurl.notfound.name") />
	<title>${title}</title>
	<meta name="nosidebar" content="true"/>

	<content tag="breadcrumb">
	</content>
</head>
<body class="jive-body-formpage">

<div id="jive-content">
	<header>
		<h1>${title}</h1>
	</header>
	<section class="jive-content-body">
		<p><@s.text name="plugin.hfurl.notfound.description" /></p>

		<p>
			<a href="javascript:history.go(-1)"><@s.text name="plugin.hfurl.notfound.link.back"/></a><br>
			<a href="<@s.url value='/community/wiki'/>"><@s.text name="plugin.hfurl.notfound.link.wiki"/></a>
		</p>
	</section>
</div>
</body>
</html>