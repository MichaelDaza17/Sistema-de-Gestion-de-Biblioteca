<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i18n" uri="i18n-tags" %>
<div class="language-selector dropdown">
    <button class="btn btn-sm btn-outline-light dropdown-toggle" type="button" id="languageDropdown" data-bs-toggle="dropdown" aria-expanded="false">
        <i class="fas fa-globe me-1"></i>
        <span>${i18n:currentLocale(pageContext.request) == 'es' ? 'Español' : 
              i18n:currentLocale(pageContext.request) == 'en' ? 'English' :
              i18n:currentLocale(pageContext.request) == 'pt' ? 'Português' : 'Italiano'}</span>
    </button>
    <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="languageDropdown">
        <li>
            <a class="dropdown-item ${i18n:currentLocale(pageContext.request) == 'es' ? 'active' : ''}" href="?locale=es">
                <span class="fi fi-es me-2"></span>Español
            </a>
        </li>
        <li>
            <a class="dropdown-item ${i18n:currentLocale(pageContext.request) == 'en' ? 'active' : ''}" href="?locale=en">
                <span class="fi fi-gb me-2"></span>English
            </a>
        </li>
        <li>
            <a class="dropdown-item ${i18n:currentLocale(pageContext.request) == 'pt' ? 'active' : ''}" href="?locale=pt">
                <span class="fi fi-br me-2"></span>Português
            </a>
        </li>
        <li>
            <a class="dropdown-item ${i18n:currentLocale(pageContext.request) == 'it' ? 'active' : ''}" href="?locale=it">
                <span class="fi fi-it me-2"></span>Italiano
            </a>
        </li>
    </ul>
</div>
<!-- CSS para las banderas de países -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/lipis/flag-icons@6.6.6/css/flag-icons.min.css" />
<style>
    .language-selector .dropdown-menu {
        min-width: 180px;
    }
    
    .language-selector .dropdown-item.active {
        background-color: #f0f0f0;
        color: #333;
    }
    
    .fi {
        width: 1.2em;
        height: 1.2em;
        margin-right: 0.5em;
        display: inline-block;
        vertical-align: middle;
    }
</style>