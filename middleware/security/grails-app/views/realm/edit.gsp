

<%@ page import="gov.nasa.podaac.security.server.Realm" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'realm.label', default: 'Realm')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${realmInstance}">
            <div class="errors">
                <g:renderErrors bean="${realmInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <g:hiddenField name="id" value="${realmInstance?.id}" />
                <g:hiddenField name="version" value="${realmInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="name"><g:message code="realm.name.label" default="Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: realmInstance, field: 'name', 'errors')}">
                                    <g:textField name="name" value="${realmInstance?.name}" />
                                </td>
                            </tr>
                            
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="description"><g:message code="realm.description.label" default="Description" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: realmInstance, field: 'description', 'errors')}">
                                    <g:textField name="description" value="${realmInstance?.description}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="roles"><g:message code="realm.roles.label" default="Roles" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: realmInstance, field: 'roles', 'errors')}">
                                    
<ul>
<g:each in="${realmInstance?.roles?}" var="r">
    <li><g:link controller="role" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></li>
</g:each>
</ul>
<g:link controller="role" action="create" params="['realm.id': realmInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'role.label', default: 'Role')])}</g:link>

                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="tokenExpiration"><g:message code="realm.tokenExpiration.label" default="Token Expiration" /></label>
                                  </br>(in days)
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: realmInstance, field: 'tokenExpiration', 'errors')}">
                                    <g:textField name="tokenExpiration" value="${fieldValue(bean: realmInstance, field: 'tokenExpiration')}" />
                                    <span>* A value of 0 will mean the token never expires.</span>
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="tokens"><g:message code="realm.tokens.label" default="Tokens" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: realmInstance, field: 'tokens', 'errors')}">
                                    
<ul>
<g:each in="${realmInstance?.tokens?}" var="t">
    <li><g:link controller="token" action="show" id="${t.id}">${t?.encodeAsHTML()}</g:link></li>
</g:each>
</ul>
<g:link controller="token" action="create" params="['realm.id': realmInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'token.label', default: 'Token')])}</g:link>

                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="verifier"><g:message code="realm.verifier.label" default="Verifier" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: realmInstance, field: 'verifier', 'errors')}">
                                    <g:select name="verifier.id" from="${gov.nasa.podaac.security.server.Verifier.list()}" optionKey="id" value="${realmInstance?.verifier?.id}"  />
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
