<!DOCTYPE html>
<html>
<head>
<#assign title = action.getText("plugin.hfurl.navigation.name") />
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
		<p><@s.text name="plugin.hfurl.navigation.description" /></p>
		<ul style="margin: 10px 20px;">
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
	</section>
</div>
</body>
</html>
