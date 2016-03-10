<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <g:render template="/common/head" />
    <g:render template="/common/dojo" />
    <g:render template="/common/css" />
    <g:render template="/common/javaScript" />
    <script type="text/javascript">
      dojo.require("dojo._base.lang");
      dojo.require("dojo._base.html");
      dojo.require("dojo.parser");
      dojo.require("dijit.form.Button");
      dojo.require("dijit.form.Form");
      dojo.require("dijit.form.ValidationTextBox");
      dojo.require("dijit.layout.LayoutContainer");
      dojo.require("dijit.layout.SplitContainer");
      dojo.require("dijit.layout.ContentPane");
      dojo.require("dijit.form.Textarea");
      dojo.require("dijit.form.FilteringSelect");
    </script>
    <style type="text/css">
      #loadContainer {
        width: 900px;
        height: 40px;
        margin: 20px 0px;
      }
      
      #loadButtonContainer {
        width: 150px;
        height: 40px;
        float: left;
      }
      
      #loadIconContainer {
        width: 650px;
        height: 40px;
        float: left;
        visibility: hidden;
      }
      
      .entryContainer {
        margin-left: auto;
        margin-right: auto;
        width: 900px;
      }
      
      .entryName {
        float: left;
        font-weight: bold;
        width: 150px;
        padding: 15px 15px;
      }
      
      .entryValue {
        float: left;
        width: 690px;
        padding: 15px 15px;
      }
      
      .highlight {
        background-color: #f5f5f5;
      }
      
      .larger {
        height: 30px;
      }
    </style>
    <script type="text/javascript">
      var loadIconContainer = 'loadIconContainer';
    
      function sendFormDone() {
        updateLoadingIcon(false);
      }
      
      function updateLoadingIcon(isLoading) {
        var visibility = 'hidden';
        if(isLoading) {
          visibility = 'visible';
        }
        
        dojo.style(loadIconContainer, 'visibility', visibility);
      }
      
      var readSuccess = function(items, request) {
        var item = items[0];
        
        console.debug('result: '+item['result']);
        
        if(item['result'] == 'OK') {
          document.location.href = "${createLink(action: 'provider')}";
        } else {
          console.debug('message: '+message);
        }
        
        sendFormDone();
      }
      
      var readError = function(items, request) {
        
        sendFormDone();
      }
      
      var receiveSuccess = function(data) {
        console.debug(data);
        
        commonReadMessage(dojo.fromJson(data), readSuccess, readError);
      }
      
      var receiveError = function(data) {
        sendFormDone();
      }
      
      function sendForm(form) {
        updateLoadingIcon(true);
        
        commonReceiveMessageForm(
          "${createLink(controller: 'provider', action: 'update')}",
          form,
          receiveSuccess,
          receiveError
        );
      }
    </script>
  </head>
  <body class="tundra">
    <div id="mainContainer">
      <div>
        <g:render template="/common/header" />
      </div>
      <div>
        <h1>
          <g:if test="${(!flash['provider'].id)}">
            Add New Provider
          </g:if>
          <g:else>
            Edit Provider
          </g:else>
        </h1>
      </div>
      <form id="providerForm" method="post">
        <g:if test="${(flash['provider'].id)}">
          <input type="hidden" name="id" value="${flash['provider'].id}" />
        </g:if>
        <div class="entryContainer">
          <div class="entryName highlight">Short Name:</div>
          <div class="entryValue highlight">
              <input type="text" name="shortName" value="${flash['provider'].shortName}" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
          </div>
        </div>
        <div class="entryContainer">
          <div class="entryName highlight">Long Name:</div>
          <div class="entryValue highlight">
            <input type="text" name="longName" value="${flash['provider'].longName}" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
          </div>
        </div>
        <div class="entryContainer">
          <div class="entryName highlight">Type:</div>
          <div class="entryValue highlight">
            <input type="text" name="type" value="${flash['provider'].type}" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
          </div>
        </div>
        <div class="entryContainer">
          <div class="entryName larger">
            <button id="updateButton" dojoType="dijit.form.Button" onclick="sendForm('providerForm');">Save</button>
          </div>
          <div class="entryValue larger">
            <div id="loadIconContainer">
              <img src="${createLinkTo(dir: 'images/monitor', file: 'Loading.gif')}" />
            </div>
          </div>
        </div>
      </form>
      <div>
        <g:render template="/common/footer" />
      </div>
    </div>
  </body>
</html>
