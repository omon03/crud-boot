const MAX_BYTE = 255;

/**
 * Функция запроса данных с сервера c помощью Fetch API
 */
function sendRequest(url, method, body) {
    const headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
    };
    return fetch(url, {
        method: method,
        body: body,
        headers: headers
    }).then(response => {
        if (response.ok && method === 'DELETE') {
            return 'ok';
        } else if (response.ok) {
            return response.json();
        } else {
            alert('Ошибка HTTP: ' + response.status);
        }
    });
}

/**
 * Функция создания ячейки в ряду
 */
function cellCreation(row, object, field) {
    let cell = document.createElement('td');
    cell.textContent = object[field];
    row.append(cell);
}

/**
 * Функция вставки данных всех пользователей в таблицу
 */
function insertUsersRows(arrayOfUsers) {
    function getButtons(userId) {
        return `<td><a class="btn"
                    data-bs-toggle="modal"
                    href="#editUserModal"
                    data-bs-userId="${userId}"
                    style="color: white; background-color: #17a2b8">Edit</a></td>\n` +
            `<td><a class="btn"
                    data-bs-toggle="modal"
                    href="#deleteUserModal"
                    data-bs-userId="${userId}"
                    style="color: white;
                    background-color: #dc3545">Delete</a></td>`;
    }

    let allUsersTableBody = document.getElementById('allUsersTableBody');

    for (let user of arrayOfUsers) {
        let row = document.createElement('tr');

        cellCreation(row, user, 'id');
        cellCreation(row, user, 'name');
        cellCreation(row, user, 'username');
        cellCreation(row, user, 'age');
        cellCreation(row, user, 'email');
        cellCreation(row, user, 'roles');

        row.insertAdjacentHTML('beforeend', getButtons(user.id));

        allUsersTableBody.append(row);
    }
}

/**
 * Функция вставки данных текущего пользователя в таблицу
 */
function insertUserRow(user) {
    let row = document.getElementById('currentUserTableBodyRow');
    cellCreation(row, user, 'id');
    cellCreation(row, user, 'name');
    cellCreation(row, user, 'username');
    cellCreation(row, user, 'age');
    cellCreation(row, user, 'email');
    cellCreation(row, user, 'roles');
}

/**
 * Функция поиска ряда в таблице по id пользователя
 */
function findRow(userId) {
    let rows = document.getElementById('allUsersTableBody').rows;
    for (let i = 0; i < rows.length; i++) {
        if (rows[i].cells[0].textContent == userId) {
            return rows[i];
        }
    }
}

/**
 * Функция замены данных в ряде таблицы
 */
function replaceUserRow(user) {
    let row = findRow(user.id);
    row.cells[0].textContent = user.id;
    row.cells[1].textContent = user.name;
    row.cells[2].textContent = user.username;
    row.cells[3].textContent = user.age;
    row.cells[4].textContent = user.email;
    row.cells[5].textContent = user.roles;
}

/**
 * Функция - достать куки
 */
function getCookie(name) {
    let matches = document.cookie.match(new RegExp(
        "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
    ));
    return matches ? decodeURIComponent(matches[1]) : undefined;
}

/**
 * Функция - Post / Patch / Delete request из формы
 */
function actionWithUser(form, action) {

    let pathVar = '';
    let body = '';

    if (action !== 'POST') {
        pathVar = '/' + form.id.value;
    }

    if (action !== 'DELETE') {
        function getRoleObjectsArray(formRoles) {
            return Array.from(formRoles)
                        .filter(option => option.selected)
                        .map(option => option.value)
                        .map(value => { return value === 'ROLE_ADMIN'
                            ? {id: 1, role: 'ROLE_ADMIN'}
                            : {id: 2, role: 'ROLE_USER'};
                        });
        }

        let user = {
            name: form.name.value,
            username: form.username.value,
            age: form.age.value,
            email: form.email.value,
            password: form.password.value,
            roles: getRoleObjectsArray(form.roles)
        };

        body = JSON.stringify(user);
    }

    sendRequest(`http://localhost:8080/users${pathVar}`, action, body)
        .then((data) => {
            if (data !== null) {
                switch(action) {
                    case 'POST':
                        alert('Пользователь добавлен');
                        insertUsersRows([data]);
                        form.reset();
                        break;
                    case 'PATCH':
                        replaceUserRow(data);
                        document.getElementById('closeEditUserModal').click();
                        form.reset();
                        break;
                }
            }
            if (data.localeCompare('ok') === 0) {
                findRow(form.id.value).remove();
                document.getElementById('closeDeleteUserModal').click();
                form.reset();
            }
        });
}

/**
 * Функция - создать лисинер, заполняющий форму
 */
function modalFormListener(formId, modalId) {
    let modal = document.getElementById(modalId);
    modal.addEventListener('show.bs.modal', (event) => {
        let form = document.getElementById(formId);
        let userId = event.relatedTarget.getAttribute('data-bs-userId');

        let row = findRow(userId);

        form.id.value = userId;
        form.name.value = row.cells[1].textContent;
        form.username.value = row.cells[2].textContent;
        form.age.value = row.cells[3].textContent;
        form.email.value = row.cells[4].textContent;
        form.roles.options[0].selected = row.cells[5].textContent.includes('USER');
        form.roles.options[1].selected = row.cells[5].textContent.includes('ADMIN');
    });
}


// Отправляем запрос на всех пользователей, вставляем инфо в таблицу
if (getCookie('isAdmin') === 'true') {
    sendRequest('http://localhost:8080/users', 'GET')
        .then(data => {
            insertUsersRows(data);
        });
}

// Отправляем запрос на текущего пользователя, вставляем инфо в таблицу
sendRequest(`http://localhost:8080/users/${getCookie('UserId')}`, 'GET')
    .then(data => {
        insertUserRow(data);
    });

// Добавляем лисенеры на заполнение форм в модалках
modalFormListener('editUserForm' ,'editUserModal');
modalFormListener('deleteUserForm', 'deleteUserModal');



/**
 * Функция рандомной смены фона элемента на некоторое время
 */
function bgColorChanger(element, maxDelta, maxColors, timeOut) {
    let rand = (min, max) => Math.round(Math.random() * (max - min) + min);
    let red = rand(0, MAX_BYTE);
    let green = rand(0, MAX_BYTE);
    let blue = rand(0, MAX_BYTE);

    let rRate = rand(- maxDelta/2, maxDelta/2);
    let gRate = rand(- maxDelta/2, maxDelta/2);
    let bRate = rand(- maxDelta/2, maxDelta/2);

    let setBG = (element, r, g, b) => element.style.background = `rgb(${r}, ${g}, ${b})`;

    for (let i = 0; i <= maxColors ; i++) {
        setTimeout(setBG, i * timeOut, element, red, green, blue);

        if (red + rRate < 0 || red + rRate > MAX_BYTE) {
            rRate = -rRate;
        }
        if (green + gRate < 0 || green + gRate > MAX_BYTE) {
            gRate = -gRate;
        }
        if (blue + bRate < 0 || blue + bRate > MAX_BYTE) {
            bRate = -bRate;
        }

        red += rRate;
        green += gRate;
        blue += bRate;
    }
    setTimeout(setBG, maxColors * timeOut + timeOut, element, MAX_BYTE, MAX_BYTE, MAX_BYTE);
}
