<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <g:render template="/common/head" />
    <g:render template="/common/dojo" />
    <g:render template="/common/css" />
    <g:render template="/common/javaScript" />
    <script type="text/javascript">
      dojo.require("dojo.parser");
      dojo.require("dijit.form.Button");
      dojo.require("dijit.form.Form");
      dojo.require("dijit.form.ValidationTextBox");
      dojo.require("dijit.layout.LayoutContainer");
      dojo.require("dijit.layout.SplitContainer");
      dojo.require("dijit.layout.ContentPane");
    </script>
    <script type="text/javascript" src="${createLinkTo(dir: 'js/monitor',file: 'home.js')}"></script>
  </head>
  <body class="tundra">
    <div id="mainContainer">
      <div>
        <g:render template="/common/header" />
      </div>
      <div>
        home
      </div>
    </div>
  </body>
</html>
