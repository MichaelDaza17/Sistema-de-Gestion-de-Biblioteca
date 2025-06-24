function initLogin() {
    // Bootstrap form validation
    (function() {
        'use strict';
        window.addEventListener('load', function() {
            var forms = document.getElementsByClassName('needs-validation');
            var validation = Array.prototype.filter.call(forms, function(form) {
                form.addEventListener('submit', function(event) {
                    if (form.checkValidity() === false) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                    form.classList.add('was-validated');
                }, false);
            });
        }, false);
    })();

    // Focus en el primer campo vacío al cargar la página
    document.addEventListener('DOMContentLoaded', function() {
        const username = document.getElementById('username');
        const password = document.getElementById('password');
        const captcha = document.getElementById('captcha');
        
        if (username && !username.value) {
            username.focus();
        } else if (password && !password.value) {
            password.focus();
        } else if (captcha) {
            captcha.focus();
        }
        
        // Limpiar el campo de CAPTCHA al hacer focus
        if (captcha) {
            captcha.addEventListener('focus', function() {
                this.select();
            });
        }
    });
}

// ============ SCRIPTS GENERALES ============
document.addEventListener('DOMContentLoaded', function() {
    // Inicializar login si estamos en esa página
    if (document.querySelector('.login-container')) {
        initLogin();
    }
    
    // Auto-cerrar alertas después de 5 segundos
    setTimeout(function() {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            if (typeof bootstrap !== 'undefined' && bootstrap.Alert) {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }
        });
    }, 5000);
    
    // Inicializar tooltips de Bootstrap
    if (typeof bootstrap !== 'undefined' && bootstrap.Tooltip) {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }
    
    // Inicializar popovers de Bootstrap
    if (typeof bootstrap !== 'undefined' && bootstrap.Popover) {
        const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
        popoverTriggerList.map(function (popoverTriggerEl) {
            return new bootstrap.Popover(popoverTriggerEl);
        });
    }
});

// ============ FUNCIONES UTILITARIAS ============

// Confirmar eliminación
function confirmDelete(url, message = '¿Está seguro de que desea eliminar este registro?') {
    if (confirm(message)) {
        window.location.href = url;
    }
    return false;
}

// Formatear números como moneda colombiana
function formatCurrency(amount) {
    return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0
    }).format(amount);
}

// Calcular días entre fechas
function daysBetween(date1, date2) {
    const oneDay = 24 * 60 * 60 * 1000;
    const firstDate = new Date(date1);
    const secondDate = new Date(date2);
    return Math.round(Math.abs((firstDate - secondDate) / oneDay));
}

// Validar documentos colombianos
function validateColombianId(id) {
    if (!id || id.length < 6 || id.length > 10) return false;
    
    // Verificar que solo contenga números
    if (!/^\d+$/.test(id)) return false;
    
    return true;
}

// Validar emails
function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

// Función para buscar en tiempo real en tablas
function initLiveSearch(inputId, tableId) {
    const input = document.getElementById(inputId);
    const table = document.getElementById(tableId);
    
    if (input && table) {
        input.addEventListener('keyup', function() {
            const filter = this.value.toLowerCase();
            const rows = table.getElementsByTagName('tr');
            
            for (let i = 1; i < rows.length; i++) { // Empezar en 1 para saltar el header
                const row = rows[i];
                const cells = row.getElementsByTagName('td');
                let found = false;
                
                for (let j = 0; j < cells.length; j++) {
                    if (cells[j].textContent.toLowerCase().indexOf(filter) > -1) {
                        found = true;
                        break;
                    }
                }
                
                row.style.display = found ? '' : 'none';
            }
        });
    }
}

// ============ SCRIPTS PARA FORMULARIOS ============

// Limpiar formulario
function clearForm(formId) {
    const form = document.getElementById(formId);
    if (form) {
        form.reset();
        form.classList.remove('was-validated');
    }
}

// Validar formulario antes de enviar
function validateForm(formId) {
    const form = document.getElementById(formId);
    if (form) {
        form.classList.add('was-validated');
        return form.checkValidity();
    }
    return false;
}

// ============ SCRIPTS PARA TABLAS ============

// Alternar selección de todas las filas
function toggleAllRows(checkbox, className) {
    const checkboxes = document.getElementsByClassName(className);
    for (let i = 0; i < checkboxes.length; i++) {
        checkboxes[i].checked = checkbox.checked;
    }
}

// Obtener filas seleccionadas
function getSelectedRows(className) {
    const selected = [];
    const checkboxes = document.getElementsByClassName(className);
    
    for (let i = 0; i < checkboxes.length; i++) {
        if (checkboxes[i].checked) {
            selected.push(checkboxes[i].value);
        }
    }
    
    return selected;
}

// ============ SCRIPTS PARA DASHBOARD ============

// Actualizar estadísticas en tiempo real
function updateStats() {
    // Esta función se puede implementar para actualizar estadísticas via AJAX
    console.log('Actualizando estadísticas...');
}

// Animación de contadores
function animateCounter(elementId, target, duration = 2000) {
    const element = document.getElementById(elementId);
    if (!element) return;
    
    const start = 0;
    const increment = target / (duration / 16); // 60 FPS
    let current = start;
    
    const timer = setInterval(function() {
        current += increment;
        element.textContent = Math.floor(current);
        
        if (current >= target) {
            element.textContent = target;
            clearInterval(timer);
        }
    }, 16);
}

// ============ SCRIPTS PARA CONSULTA PÚBLICA ============

// Filtrar libros en tiempo real
function filterBooks() {
    const searchInput = document.getElementById('searchInput');
    const categorySelect = document.getElementById('categorySelect');
    const bookCards = document.getElementsByClassName('book-card');
    
    if (!searchInput || !bookCards) return;
    
    const searchTerm = searchInput.value.toLowerCase();
    const selectedCategory = categorySelect ? categorySelect.value : '';
    
    for (let i = 0; i < bookCards.length; i++) {
        const card = bookCards[i];
        const title = card.querySelector('.card-title').textContent.toLowerCase();
        const author = card.querySelector('.card-text').textContent.toLowerCase();
        const category = card.getAttribute('data-category');
        
        const matchesSearch = title.indexOf(searchTerm) > -1 || author.indexOf(searchTerm) > -1;
        const matchesCategory = selectedCategory === '' || category === selectedCategory;
        
        card.closest('.col-md-4').style.display = (matchesSearch && matchesCategory) ? '' : 'none';
    }
}

// ============ INICIALIZACIÓN GLOBAL ============

// Inicializar scripts cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    // Agregar clases de animación a elementos
    const cards = document.querySelectorAll('.card');
    cards.forEach(function(card, index) {
        setTimeout(function() {
            card.classList.add('fade-in');
        }, index * 100);
    });
    
    // Inicializar búsqueda en vivo si existe
    if (document.getElementById('searchInput')) {
        const searchInput = document.getElementById('searchInput');
        const categorySelect = document.getElementById('categorySelect');
        
        if (searchInput) {
            searchInput.addEventListener('keyup', filterBooks);
        }
        
        if (categorySelect) {
            categorySelect.addEventListener('change', filterBooks);
        }
    }
});

// ============ MANEJO DE ERRORES ============

// Manejo global de errores
window.addEventListener('error', function(e) {
    console.error('Error en la aplicación:', e.error);
});

// Función para mostrar mensajes de error
function showError(message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-danger alert-dismissible fade show';
    alertDiv.innerHTML = `
        <i class="fas fa-exclamation-triangle me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    const container = document.querySelector('.main-content') || document.body;
    container.insertBefore(alertDiv, container.firstChild);
    
    // Auto-cerrar después de 5 segundos
    setTimeout(function() {
        if (alertDiv.parentNode) {
            alertDiv.parentNode.removeChild(alertDiv);
        }
    }, 5000);
}

// Función para mostrar mensajes de éxito
function showSuccess(message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-success alert-dismissible fade show';
    alertDiv.innerHTML = `
        <i class="fas fa-check-circle me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    const container = document.querySelector('.main-content') || document.body;
    container.insertBefore(alertDiv, container.firstChild);
    
    // Auto-cerrar después de 3 segundos
    setTimeout(function() {
        if (alertDiv.parentNode) {
            alertDiv.parentNode.removeChild(alertDiv);
        }
    }, 3000);
}