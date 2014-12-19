Jive SBS Plugin: Human Friendly URLs
====================================

Jive SBS plugin providing human friendly URLs for documents

Installation steps
------------------

1. Install the plugin via the administration console (System -> Plugins -> Add Plugin)
2. Restart the application
3. Go to the administration console -> System -> Management -> Human friendly URLs and find caption "Create new index of human friendly URLs for published documents:".
   Click the button labelled "Update index".
4. Optionally install human friendly urls links extension which generates HF URLs on pages. See https://github.com/jbossorg/sbs-human-friendly-urls-links
5. Add rule to the Apache rewrite module which redirects every /docs/DOC-1234 request to HF URL and then reload apache configuration:


		RewriteCond %{QUERY_STRING} !^.*uniqueTitle=false.*$
		RewriteCond %{REQUEST_URI} !^.*/restore.*$
		RewriteCond %{REQUEST_URI} !^.*/delete.*$
		RewriteRule ^/docs/DOC-(.*) /hfurl/redirectToHFURL.jspa?url=/docs/DOC-$1&params=%{QUERY_STRING} [R=301,L]


