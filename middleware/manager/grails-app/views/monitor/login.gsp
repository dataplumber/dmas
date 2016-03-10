<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <g:render template="/common/head" />
    <g:render template="/common/dojo" />
    <g:render template="/common/css" />
    <script type="text/javascript">
      dojo.require("dojo.parser");
      dojo.require("dijit.form.Button");
      dojo.require("dijit.form.Form");
      dojo.require("dijit.form.ValidationTextBox");
      dojo.require("dijit.layout.LayoutContainer");
      dojo.require("dijit.layout.ContentPane");
    </script>
    <style type="text/css">
      .container {
        margin-left: auto;
        margin-right: auto;
        width: 383px;
      }
      
      .entryContainer {
        width: 383px;
      }
      
      .entryName {
        float: left;
        /*font-weight: bold;*/
        width: 100px;
        height: 30px;
        padding: 15px 15px;
        font-size: 130%;
      }
      
      .entryValue {
        float: left;
        width: 223px;
        height: 30px;
        padding: 15px 15px;
      }
      
      .highlight {
        background-color: #f5f5f5;
      }
      
      #message {
        background-color: #ffff88;
        padding: 15px 15px;
        color: #414141;
      }
    </style>
  </head>
  <body class="tundra">
    <div class="container">
      <div class="entryContainer" style="height: 100px">
        
      </div>
      <div class="entryContainer">
        <img src="${createLinkTo(dir: 'images/monitor', file: 'LOGO-TOP.jpg')}" border="0" />
      </div>
      <div class="entryContainer">
        <g:if test="${flash.containsKey('login.message')}">
          <div id="message">
            ${flash['login.message']}
          </div>
        </g:if>
      </div>
      <form action="${createLink(action: 'login')}" method="post" dojoType="dijit.form.Form">
      <div class="entryContainer">
          <div class="entryName highlight">
            Username
          </div>
          <div class="entryValue highlight">
            <input type="text" name="username" style="padding: 5px;" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
          </div>
      </div>
      <div class="entryContainer">
          <div class="entryName highlight">
            Password
          </div>
          <div class="entryValue highlight">
            <input type="password" name="password" style="padding: 5px;" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
          </div>
      </div>
      <div class="entryContainer">
          <div class="entryName highlight">
            
          </div>
          <div class="entryValue highlight">
            <button type="submit" dojoType="dijit.form.Button">
              <div style="width: 80px;">Login</div>
            </button>
          </div>
      </div>
      </form>
    </div>
  </body>
</html>
