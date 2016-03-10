<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <g:render template="/common/head" />
    <g:render template="/common/dojo" />
    <g:render template="/common/css" />
    <g:render template="/common/javaScript" />
    <script type="text/javascript">
      //dojo.require("dojo._base.lang");
      //dojo.require("dojo._base.html");
      dojo.require("dojo.parser");
      dojo.require("dijit.form.Button");
      dojo.require("dijit.form.Form");
      dojo.require("dijit.form.ValidationTextBox");
      dojo.require("dijit.layout.LayoutContainer");
      dojo.require("dijit.layout.SplitContainer");
      dojo.require("dijit.layout.ContentPane");
      dojo.require("dijit.form.TextBox");
      dojo.require("dijit.Dialog");
    </script>
    <style type="text/css">
      #loadContainer {
        width: 54px;
        margin-left: auto;
        margin-right: auto;
        /*margin: 20px 0px;*/
      }
      
      /*
      #loadButtonContainer {
        width: 150px;
        height: 40px;
        float: left;
      }
      */
      
      #loadIconContainer {
        width: 24px;
        padding: 30px 30px;
        float: left;
        visibility: hidden;
      }
      
      /*
      #loadMessageContainer {
        width: 416px;
        padding: 15px 15px;
        float: left;
        visibility: hidden;
      }
      */
      
      #messageTable {
        margin-left: auto;
        margin-right: auto;
        width: 780px;
        opacity: 0;
        clear: both;
      }
      
      .messageTableHeader {
        font-weight: bold;
        color: #fafafa;
        overflow: auto;
      }
      
      .messageTableHeaderColumn-Importance {
        float: left;
        width: 60px;
        padding: 10px 10px;
        background-color: #4d4c4d;
      }
      
      .messageTableHeaderColumn-ShortName {
        float: left;
        width: 140px;
        padding: 10px 10px;
        background-color: #4d4c4d;
      }
      
      .messageTableHeaderColumn-LongName {
        float: left;
        width: 400px;
        padding: 10px 10px;
        background-color: #4d4c4d;
      }
      
      .messageTableHeaderColumn-Type {
        float: left;
        width: 100px;
        padding: 10px 10px;
        background-color: #4d4c4d;
      }
      
      .messageTableRow {
        overflow: auto;
      }
      
      .messageTableColumn-importance {
        float: left;
        width: 60px;
        height: 60px;
        padding: 10px 10px;
      }
      
      .messageTableColumn-shortName {
        float: left;
        width: 140px;
        height: 60px;
        padding: 10px 10px;
      }
      
      .messageTableColumn-longName {
        float: left;
        width: 400px;
        height: 60px;
        padding: 10px 10px;
      }
      
      .messageTableColumn-type {
        float: left;
        width: 100px;
        height: 60px;
        padding: 10px 10px;
      }
      
      .messageTableMessage {
        width: 760px;
        padding: 10px 10px;
        background-color: #efefef;
        overflow: auto;
      }
      
      .messageTable-High {
        background-color: #ffbdbd;
        border-bottom: 1px solid #a0a0a0;
      }
      
      .messageTable-Medium {
        background-color: #fffbbd;
        border-bottom: 1px solid #ededed;
      }
      
      .messageTable-Low {
        background-color: #d0ffbd;
        border-bottom: 1px solid #808080;
      }
      
      #messageMenu {
        text-align: right;
        margin: 10px 0px;
      }

      #messageMenu ul {
        margin: 0px;
        padding: 0px;
        list-style-type: none;
      }

      #messageMenu ul li {
        display: inline;
      }

      #messageMenu ul li a {
        text-decoration: none;
        margin: 5px 10px;
        color: #000000;
      }
      
      #errorDialogMessage {
        padding: 15px 15px;
        width: 300px;
        height: 100px;
        overflow: auto;
      }
    </style>
    <script type="text/javascript">
      var messageNames = [
        'importance', 'shortName', 'longName', 'type'
      ];
      var messageNameImportance = 'importance';
      var loadIconContainer = 'loadIconContainer';
      var messageTableRows = 'messageTableRows';
      var messageTableColorName = 'messageTable';
      var messageTableRowName = 'messageTableRow';
      var messageTableColumnName = 'messageTableColumn';
      var messageTableName = 'messageTable';
      var messageTable = 'messageTable';
      var messageTableMessageName = 'messageTableMessage';
      var errorDialog = 'errorDialog';
      var errorDialogMessage = 'errorDialogMessage';
    
      var messageReceivedSucceeded = function(items, request) {
        var messagesNode = dojo.byId(messageTableRows);
        while(messagesNode.firstChild) {
          messagesNode.removeChild(messagesNode.firstChild);
        }
        
        for(var i = 0; i < items.length; i++) {
          var item = items[i];
          
          // all other status
          var newRowNode = dojo.doc.createElement('div');
          dojo.addClass(newRowNode, messageTableRowName);
          messagesNode.appendChild(newRowNode);
          
          for(var j = 0; j < messageNames.length; j++) {
            var messageName = messageNames[j];
            
            var newColumnNode = dojo.doc.createElement('div');
            newColumnNode.innerHTML = item[messageName];
            dojo.addClass(newColumnNode, messageTableColumnName+'-'+messageName);
            dojo.addClass(newColumnNode, messageTableName+'-'+item[messageNameImportance]);
            newRowNode.appendChild(newColumnNode);
          }
          
          // message
          var newMessageRowNode = dojo.doc.createElement('div');
          var newMessageRowNodeId = messageTableRowName+"-"+i;
          dojo.attr(newMessageRowNode, 'id', newMessageRowNodeId);
          dojo.addClass(newMessageRowNode, messageTableRowName);
          dojo.style(newMessageRowNode, 'display', 'none')
          //dojo.connect(newRowNode, 'onclick', dojo.hitch(null, 'showElement', newMessageRowNodeId));
          messagesNode.appendChild(newMessageRowNode);
          
          var newMessageNode = dojo.doc.createElement('div');
          dojo.addClass(newMessageNode, messageTableMessageName);
          newMessageRowNode.appendChild(newMessageNode);
          
          var newLinkContainerNode = dojo.doc.createElement('div');
          dojo.style(newLinkContainerNode, 'width', '100%');
          dojo.style(newLinkContainerNode, 'text-align', 'right');
          newMessageNode.appendChild(newLinkContainerNode);
          
          var newLinkNode = dojo.doc.createElement('a');
          dojo.attr(newLinkNode, 'href', "${createLink(action: 'providerEdit')}"+"?id="+item['id']);
          newLinkNode.innerHTML = 'Edit';
          newLinkContainerNode.appendChild(newLinkNode);
        }
        
        var messageTableNode = dojo.byId(messageTable);
        dojo.style(messageTableNode, 'opacity', 0);
        dojo.fadeIn({node: messageTableNode, duration: 500}).play();
        
        updateProviderDone();
      }
      
      var messageReceivedFailed = function(arg1, arg2) {
        updateProviderDone();
        
        dojo.byId(errorDialogMessage).innerHTML = arg1.message;
        dijit.byId(errorDialog).show();
      }
      
      function updateProvider() {
        updateLoadingIcon(true);
        
        commonReceiveMessage(
          commonDataSourceUrls['provider'],
          messageReceivedSucceeded,
          messageReceivedFailed
        );
      }
      
      function updateProviderDone() {
        updateLoadingIcon(false);
      }
      
      function updateLoadingIcon(isLoading) {
        var visibility = 'hidden';
        if(isLoading) {
          visibility = 'visible';
        }
        
        dojo.style(loadIconContainer, 'visibility', visibility);
      }
      
      function showElement(id) {
        //console.debug('id: '+id);
        var targetNode = dojo.byId(id);
        
        var display = 'block';
        if(dojo.style(targetNode, 'display').toLowerCase() != 'none') {
          display = 'none';
        }
        dojo.style(targetNode, 'display', display);
      }
      
      dojo.addOnLoad(function() {
        updateProvider();
      });
    </script>
  </head>
  <body class="tundra">
    <div id="mainContainer">
      <div>
        <div dojoType="dijit.Dialog" id="errorDialog" title="Error">
          <div id="errorDialogMessage">
          </div>
        </div>
      </div>
      <div>
        <g:render template="/common/header" />
      </div>
      <div>
        <div id="loadContainer">
          <!--
          <div id="loadButtonContainer">
            <button dojoType="dijit.form.Button" onclick="updateProvider();">Update</button>
          </div>
          -->
          <div id="loadIconContainer">
            <img id="loadIcon" src="${createLinkTo(dir: 'images/monitor', file: 'Loading.gif')}" />
          </div>
        </div>
      </div>
      <!--
      <div>
        <div id="messageContainer">
        </div>
      </div>
      -->
      <!--
      <div id="messageMenu">
        <ul>
          <li><a href="${createLink(action: 'providerEdit')}">Add New Provider</a></li>
        </ul>
      </div>
      -->
      <div>
        <div id="messageTable">
          <div class="messageTableHeader">
            <div class="messageTableHeaderColumn-Importance">Importance</div>
            <div class="messageTableHeaderColumn-ShortName">Short Name</div>
            <div class="messageTableHeaderColumn-LongName">Long Name</div>
            <div class="messageTableHeaderColumn-Type">Type</div>
          </div>
          <div id="messageTableRows">
          </div>
        </div>
      </div>
      <div>
        <g:render template="/common/footer" />
      </div>
    </div>
  </body>
</html>
