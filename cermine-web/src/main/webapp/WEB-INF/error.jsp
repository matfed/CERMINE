<%@ page    language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib  prefix="c"      uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib  prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib  prefix="spring" uri="http://www.springframework.org/tags" %>

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <%@include file="html_meta.jsp" %>
        <!-- eof blueimp fileUpload css-->

        <title>Content ExtRactor and MINEr - User Console</title>
    </head>
    <body>
        <div id="wrapper">
            <%@include file="header.jsp" %>
            <article id="main">
                <h1>Error occured!</h1>
                Sorry, an unexpected error occured:
                <div class="ui-widget">
                    <div class="ui-state-error ui-corner-all" style="padding: 0 .7em;">
                        <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                            ${errorMessage}</p>
                    </div>
                </div>
            </article>
            <%@include file="footer.jsp" %>
        </div>
    </body>
</html>