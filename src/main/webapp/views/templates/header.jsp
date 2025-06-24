<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.titulo} - Sistema de Biblioteca</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <!-- DataTables -->
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.13.4/css/dataTables.bootstrap5.min.css">
    <!-- Estilos personalizados -->
    <style>
        body {
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        .sidebar {
            background-color: #343a40;
            min-height: calc(100vh - 56px);
            box-shadow: 2px 0 5px rgba(0,0,0,0.1);
        }
        .sidebar .nav-link {
            color: #ddd;
            border-radius: 0;
            transition: all 0.3s;
        }
        .sidebar .nav-link:hover {
            background-color: #495057;
            color: white;
        }
        .sidebar .nav-link.active {
            background-color: #0d6efd;
            color: white;
        }
        .sidebar .nav-link i {
            margin-right: 10px;
        }
        .navbar-brand {
            padding-left: 15px;
        }
        .main-content {
            flex: 1;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .card {
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .card-header {
            background-color: #f8f9fa;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container-fluid">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">
                <i class="fas fa-book-open"></i> Sistema de Biblioteca
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <!-- Selector de idioma -->
                    <li class="nav-item">
                        <jsp:include page="/views/components/language_selector.jsp" />
                    </li>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown">
                            <i class="fas fa-user-circle"></i> ${sessionScope.usuario.nombreCompleto}
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/usuarios?accion=perfil">
                                <i class="fas fa-id-card"></i> Mi Perfil
                            </a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
                                <i class="fas fa-sign-out-alt"></i> Cerrar Sesión
                            </a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
    
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3 col-lg-2 d-md-block bg-dark sidebar collapse">
                <div class="position-sticky pt-3">
                    <ul class="nav flex-column">
                        <li class="nav-item">
                            <a class="nav-link ${param.menu == 'dashboard' ? 'active' : ''}" href="${pageContext.request.contextPath}/dashboard">
                                <i class="fas fa-tachometer-alt"></i> Dashboard
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${param.menu == 'libros' ? 'active' : ''}" href="${pageContext.request.contextPath}/libros">
                                <i class="fas fa-book"></i> Libros
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${param.menu == 'lectores' ? 'active' : ''}" href="${pageContext.request.contextPath}/lectores">
                                <i class="fas fa-users"></i> Lectores
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${param.menu == 'prestamos' ? 'active' : ''}" href="${pageContext.request.contextPath}/prestamos">
                                <i class="fas fa-exchange-alt"></i> Préstamos
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${param.menu == 'categorias' ? 'active' : ''}" href="${pageContext.request.contextPath}/categorias">
                                <i class="fas fa-tags"></i> Categorías
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/consulta-publica" target="_blank">
                                <i class="fas fa-search"></i> Consulta Pública
                            </a>
                        </li>
                        <c:if test="${sessionScope.usuario.rol == 'ADMINISTRADOR'}">
                            <li class="nav-item">
                                <a class="nav-link ${param.menu == 'usuarios' ? 'active' : ''}" href="${pageContext.request.contextPath}/usuarios">
                                    <i class="fas fa-user-cog"></i> Usuarios Sistema
                                </a>
                            </li>
                        </c:if>
                    </ul>
                </div>
            </div>
            
            <!-- Main content -->
            <div class="col-md-9 ms-sm-auto col-lg-10 px-md-4 main-content">
                <!-- Messages -->
                <c:if test="${not empty success}">
                    <div class="alert alert-success alert-dismissible fade show my-3" role="alert">
                        <i class="fas fa-check-circle"></i> ${success}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                
                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show my-3" role="alert">
                        <i class="fas fa-exclamation-circle"></i> ${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                
                <c:if test="${not empty warning}">
                    <div class="alert alert-warning alert-dismissible fade show my-3" role="alert">
                        <i class="fas fa-exclamation-triangle"></i> ${warning}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                
                <c:if test="${not empty info}">
                    <div class="alert alert-info alert-dismissible fade show my-3" role="alert">
                        <i class="fas fa-info-circle"></i> ${info}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                
                <!-- Page title -->
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h1 class="h2"><i class="${param.icono}"></i> ${param.titulo}</h1>
                    <c:if test="${not empty param.accionNuevo}">
                        <a href="${param.accionNuevo}" class="btn btn-primary">
                            <i class="fas fa-plus-circle"></i> ${param.textoNuevo}
                        </a>
                    </c:if>
                </div>