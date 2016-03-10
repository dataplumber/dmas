<!--<link href="${createLinkTo(dir: 'css/monitor', file: 'monitor.css')}" rel="stylesheet" type="text/css" />-->
<style type="text/css">
  html, body {
    font-family: "Lucida Grande", "Lucida Sans Unicode", Arial, Verdana, sans-serif;
    font-size: 12px;
    line-height: 18px;
    /*
    width: 90%;
    height: 100%;
    border: 0px;
    padding: 0px;
    margin: 0px;
    */
  }

  #mainContainer {
    margin-left: auto;
    margin-right: auto;
    width: 900px;
  }

  #headerActions {
    text-align: right;
  }

  #headerActions ul {
    list-style-type: none;
  }

  #headerActions ul li {
    padding: 15px 15px;
    display: inline;
  }

  #headerActions ul li a {
    text-decoration: none;
    font-size: 120%;
    color: #000000;
  }

  #headerActions ul li a:hover {
    border-bottom: 2px solid #66aaff;
  }

  #headerMenu {
    margin-left: auto;
    margin-right: auto;
    width: 720px;
  }

  #headerMenu ul {
    list-style-type: none;
    margin: 0px;
    padding: 0px;
  }

  #headerMenu ul li {
    float: left;
  }

  #headerMenu ul li a {
    background-image: url(${createLinkTo(dir: 'images/monitor', file: 'Menu.png')});
    text-decoration: none;
    width: 180px;
    height: 40px;
    text-align: center;
    color: #dadada;
    display: block;
  }
  
  #headerMenu ul li a div {
    padding: 10px;
  }

  #headerMenu ul li a:hover {
    color: #ffffff;
  }
  
  .footerContainer {
    padding: 50px 50px;
  }
</style>
