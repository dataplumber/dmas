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
      
      .subHeader {
        color: #9a9a9a;
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
          document.location.href = "${createLink(action: 'dataLatency')}";
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
          "${createLink(controller: 'dataLatency', action: 'update')}",
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
          <g:if test="${(!flash['dataset'].getDatasetId())}">
            Add New Dataset
          </g:if>
          <g:else>
            Edit Dataset
          </g:else>
        </h1>
      </div>
      <form id="dataLatencyForm" method="post">
        <g:if test="${(flash['dataset'].getDatasetId())}">
          <input type="hidden" name="id" value="${flash['dataset'].getDatasetId()}" />
        </g:if>
        <div>
          <h2 class="subHeader">Dataset</h2>
        </div>
        <div class="entryContainer">
          <div class="entryName highlight">Short Name:</div>
          <div class="entryValue highlight">
            <input type="text" name="datasetShortName" value="${flash['dataset'].getShortName()}" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
          </div>
        </div>
        <div class="entryContainer">
          <div class="entryName highlight">Long Name:</div>
          <div class="entryValue highlight">
            <input type="text" name="datasetLongName" value="${flash['dataset'].getLongName()}" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
          </div>
        </div>
        <div class="entryContainer">
          <div class="entryName highlight">Processing Level:</div>
          <div class="entryValue highlight">
            <input type="text" name="datasetProcessingLevel" value="${flash['dataset'].getProcessingLevel()}" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
          </div>
        </div>
        <div class="entryContainer">
          <div class="entryName highlight">Region:</div>
          <div class="entryValue highlight">
            <input type="text" name="datasetRegion" value="${flash['dataset'].getRegion()}" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
          </div>
        </div>
        <div class="entryContainer">
          <div class="entryName highlight">Latitude Resolution:</div>
          <div class="entryValue highlight">
            <input type="text" name="datasetLatitudeResolution" value="${flash['dataset'].getLatitudeResolution()}" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
          </div>
        </div>
        <div class="entryContainer">
          <div class="entryName highlight">Longitude Resolution:</div>
          <div class="entryValue highlight">
            <input type="text" name="datasetLongitudeResolution" value="${flash['dataset'].getLongitudeResolution()}" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
          </div>
        </div>
        <div>
          <h2 class="subHeader">Provider</h2>
        </div>
        <div class="entryContainer">
          <div class="entryName highlight">Provider</div>
          <div class="entryValue highlight">
            <select dojoType="dijit.form.FilteringSelect" style="width: 200px;" name="providerProvider" autocomplete="false">
              <g:each var="provider" in="${flash['providers']}">
                <option value="${provider['id']}">${provider['shortName']}</option>
              </g:each>
            </select>
          </div>
        </div>
        <div>
          <h2 class="subHeader">Location Policy</h2>
        </div>
        <g:each status="i" var="locationPolicy" in="${flash['locationPolicySet']}">
          <div class="entryContainer">
            <div class="entryName highlight">Base Path ${i}:</div>
            <div class="entryValue highlight">
              <input type="text" name="locationPolicyBasePath${i}" value="${locationPolicy.getBasePath()}" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
            </div>
          </div>
          <div class="entryContainer">
            <div class="entryName highlight">Type ${i}:</div>
            <div class="entryValue highlight">
              <input type="text" name="locationPolicyType${i}" value="${locationPolicy.getType()}" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
            </div>
          </div>
        </g:each>
        <div>
          <h2 class="subHeader">Policy</h2>
        </div>
        <div class="entryContainer">
          <div class="entryName highlight">Data Class:</div>
          <div class="entryValue highlight">
            <input type="text" name="policyDataClass" value="${flash['policy'].getDataClass()}" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
          </div>
        </div>
        <div class="entryContainer">
          <div class="entryName highlight">Access Type:</div>
          <div class="entryValue highlight">
            <input type="text" name="policyAccessType" value="${flash['policy'].getAccessType()}" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
          </div>
        </div>
        <div class="entryContainer">
          <div class="entryName highlight">Base Path Append Type:</div>
          <div class="entryValue highlight">
            <input type="text" name="policyBasePathAppendType" value="${flash['policy'].getBasePathAppendType()}" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
          </div>
        </div>
        <div class="entryContainer">
          <div class="entryName highlight">Data Format:</div>
          <div class="entryValue highlight">
            <input type="text" name="policyDataFormat" value="${flash['policy'].getDataFormat()}" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
          </div>
        </div>
        <div class="entryContainer">
          <div class="entryName highlight">Compress Type:</div>
          <div class="entryValue highlight">
            <input type="text" name="policyCompressType" value="${flash['policy'].getCompressType()}" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
          </div>
        </div>
        <div class="entryContainer">
          <div class="entryName highlight">Checksum Type:</div>
          <div class="entryValue highlight">
            <input type="text" name="policyChecksumType" value="${flash['policy'].getChecksumType()}" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
          </div>
        </div>
        <div class="entryContainer">
          <div class="entryName highlight">Spatial Type:</div>
          <div class="entryValue highlight">
            <input type="text" name="policySpatialType" value="${flash['policy'].getSpatialType()}" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
          </div>
        </div>
        <div class="entryContainer">
          <div class="entryName highlight">Access Constraint:</div>
          <div class="entryValue highlight">
            <input type="text" name="policyAccessConstraint" value="${flash['policy'].getAccessConstraint()}" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
          </div>
        </div>
        <div class="entryContainer">
          <div class="entryName highlight">Use Constraint:</div>
          <div class="entryValue highlight">
            <input type="text" name="policyUseConstraint" value="${flash['policy'].getUseConstraint()}" dojoType="dijit.form.ValidationTextBox" required="true" invalidMessage="Cannot be empty!" />
          </div>
        </div>
        <div class="entryContainer">
          <div class="entryName larger">
            <button id="updateButton" dojoType="dijit.form.Button" onclick="sendForm('dataLatencyForm');">Save</button>
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
