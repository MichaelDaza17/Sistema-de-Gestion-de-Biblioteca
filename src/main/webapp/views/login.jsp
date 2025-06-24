<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar Sesión - Sistema de Biblioteca</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
</head>
<body class="login-page">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-5 col-md-7">
                <div class="login-container">
                    <!-- Header -->
                    <div class="login-header">
                        <i class="fas fa-book-open fa-3x mb-3"></i>
                        <h2 class="mb-0">Sistema de Biblioteca</h2>
                        <p class="mb-0 opacity-75">Acceso para Bibliotecarios</p>
                    </div>
                    
                    <!-- Form -->
                    <div class="login-form">
                        <!-- Alerts -->
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                ${error}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <form method="post" action="${pageContext.request.contextPath}/login" class="needs-validation" novalidate>
                            <!-- Username -->
                            <div class="form-floating mb-3">
                                <input type="text" class="form-control" id="username" name="username" 
                                       value="${username}" placeholder="Usuario" required>
                                <label for="username">
                                    <i class="fas fa-user me-2"></i>Usuario
                                </label>
                                <div class="invalid-feedback">
                                    Por favor ingrese su usuario
                                </div>
                            </div>

                            <!-- Password -->
                            <div class="form-floating mb-3">
                                <input type="password" class="form-control" id="password" name="password" 
                                       placeholder="Contraseña" required>
                                <label for="password">
                                    <i class="fas fa-lock me-2"></i>Contraseña
                                </label>
                                <div class="invalid-feedback">
                                    Por favor ingrese su contraseña
                                </div>
                            </div>

                            <!-- CAPTCHA -->
                            <div class="captcha-container">
                                <label class="form-label fw-bold">
                                    <i class="fas fa-shield-alt me-2"></i>Código de Verificación
                                </label>
                                <div class="mb-2">
                                    <img src="${captchaImage}" alt="CAPTCHA" class="captcha-image" 
                                         title="Código de verificación">
                                </div>
                                <div class="form-floating">
                                    <input type="text" class="form-control" id="captcha" name="captcha" 
                                           placeholder="Ingrese el código" required maxlength="6" autocomplete="off">
                                    <label for="captcha">Ingrese el código mostrado</label>
                                    <div class="invalid-feedback">
                                        Por favor ingrese el código de verificación
                                    </div>
                                </div>
                                <small class="text-muted">
                                    <i class="fas fa-info-circle me-1"></i>
                                    Ingrese exactamente los caracteres mostrados en la imagen
                                </small>
                            </div>

                            <!-- Submit Button -->
                            <div class="d-grid gap-2 mt-4">
                                <button type="submit" class="btn btn-primary btn-login">
                                    <i class="fas fa-sign-in-alt me-2"></i>
                                    Iniciar Sesión
                                </button>
                            </div>
                        </form>

                        <!-- Links -->
                        <div class="text-center mt-4">
                            <hr>
                            <a href="${pageContext.request.contextPath}/" class="text-decoration-none">
                                <i class="fas fa-home me-2"></i>Volver al Inicio
                            </a>
                            <span class="mx-3">|</span>
                            <a href="${pageContext.request.contextPath}/consulta-publica" class="text-decoration-none">
                                <i class="fas fa-search me-2"></i>Consulta Pública
                            </a>
                        </div>
                    </div>
                </div>
                
                <!-- Footer Info -->
                <div class="text-center mt-4">
                    <p class="text-white-50 mb-0">
                        <i class="fas fa-graduation-cap me-2"></i>
                        SENA - Análisis y Desarrollo de Software
                    </p>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>