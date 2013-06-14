<html>
    <head>
        <#assign pageTitle=action.getText('plugin.hfurl.admin.hf-urls.name') />
        <title>${pageTitle}</title>
        <content tag="pagetitle">${pageTitle}</content>
        <content tag="pageID">system-hf-urls</content>
    </head>
    <body>
        <#include "/template/global/include/form-message.ftl" />

        <p>
         Count of indexed URLs:&nbsp;<@s.property value="indexedURLs"/>
        </p>
        <@s.form theme="simple" action="human-friendly-urls-reindex">
          <table>
            <tr>
              <td><@s.text name="plugin.hfurl.admin.hf-urls.reindex.description"/>:</td>
              <td><@s.submit value="${action.getText('plugin.hfurl.admin.hf-urls.reindex.submit')}"/></td>
            </tr>
          </table>
        </@s.form>
        <#if enabled>
        <@s.form theme="simple" action="human-friendly-urls-disable">
          <table>
            <tr>
              <td><@s.text name="plugin.hfurl.admin.hf-urls.enabled.description"/>:</td>
              <td><@s.submit value="${action.getText('plugin.hfurl.admin.hf-urls.enabled.status.change2disabled')}" /></td>
            </tr>
          </table>
        </@s.form>
        <#else>
        <@s.form theme="simple" action="human-friendly-urls-enable">
          <table>
            <tr>
              <td><@s.text name="plugin.hfurl.admin.hf-urls.enabled.description"/>:</td>
              <td><@s.submit value="${action.getText('plugin.hfurl.admin.hf-urls.enabled.status.change2enabled')}" /></td>
            </tr>
          </table>
        </@s.form>
        </#if>
    </body>
</html>