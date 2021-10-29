function showPhoto(input) {
    var reader = new FileReader();
    reader.onload = function (e) {
        $('#photo').attr('src', e.target.result);
    }
    reader.readAsDataURL(input.files[0]);
    $('#photo').show();
}

function clearInput() {
    let mark = document.getElementById('mark');
    mark.value = '';
}

function setUserName() {
    $.ajax({
        type: 'GET',
        url: 'http://localhost:8080/cars/user.do',
        dataType: 'json'
    }).done(function (response) {
        if (response != undefined) {
            document.getElementById('userOut').innerHTML = response.name + ' | Выйти';
            authUserEmail = response.email;
        }
    }).fail(function (err) {
        alert('Пользователь не авторизован!');
        console.log(err);
    });
}

function reloadItems() {
    let filterSelection = document.getElementById('filter');
    let filterParam = '';
    let selectedValue = filterSelection.options[filterSelection.selectedIndex].value;
    if (selectedValue != undefined) {
        filterParam = selectedValue;
    }
    let markParam = '';
    let markValue = document.getElementById('mark').value;
    if (markValue != undefined) {
        markParam = markValue;
    }
    let box = document.getElementById('box');
    box.innerHTML = '<div></div>';
    $.ajax({
        type: 'GET',
        url: 'http://localhost:8080/cars/announce' + '?filter=' + filterParam + '&mark=' + markParam,
        dataType: 'json'
    }).done(function (data) {
        for (let item of data) {
            addRow(item);
        }
    }).fail(function (err) {
        console.log(err);
    });
}

function bin2String(array) {
    var result = "";
    for (var i = 0; i < array.length; i++) {
        result += String.fromCharCode(array[i]);
    }
    return result;
}

function addRow(item) {
    let id = item.id;
    let description = item.description;
    let image;
    let images = item.images;
    if (images.length != 0) {
        image = bin2String(images[0].photo);
    }
    let mark = item.car.model.mark.name;
    let model = item.car.model.name;
    let bodyType = item.car.model.bodyType.name;
    let transmission = item.car.model.transmission.name;
    let colour = item.car.colour;
    let price = item.price;
    let created = item.created;
    let sold = item.sold == true ? 'Продано' : 'В продаже';
    let soldButton = item.sold == true ? 'Вернуть в продажу' : 'Снять с публикации';
    let visibleOfManageZone = item.user.email == authUserEmail ? '' : 'hidden';
    var card = document.createElement("div");
    card.className = 'card card-margin border mb-3';
    card.setAttribute("id", id);
    card.innerHTML =
        '<div class="card-header">' + item.car.name + '</div>' +
        '<div class="card-body">' +
        '<div class="row">' +
        '<div class="col-4">' +
        '<img src="data:image/jpg;base64, ' + image + '"' + ' align="middle" width="300px" height="200px" alt="No image" onerror="this.onerror=null;this.src=\'img/noPhoto.jpeg\';"/>' +
        '</div>' +
        '<div class="col-8">' +
        '<p class="card-text"><b>Описание: </b>' + description + '</p>' +
        '<p class="card-text"><b>Марка: </b>' + mark + '</p>' +
        '<p class="card-text"><b>Модель: </b>' + model + '</p>' +
        '<p class="card-text"><b>Кузов: </b>' + bodyType + '</p>' +
        '<p class="card-text"><b>Коробка передач: </b>' + transmission + '</p>' +
        '<p class="card-text"><b>Цвет: </b>' + colour + '</p>' +
        '<p class="card-text"><b>Цена: </b>' + price + '</p>' +
        '<p class="card-text"><b>Дата публикации: </b>' + created + '</p>' +
        '<p class="card-text"><b>Статус объявления: </b>' + sold + '</p>' +
        '</div>' +
        '</div>' +
        '<div class="row justify-content-md-end" id="manage"' + visibleOfManageZone + '>' +
        '<button class="btn btn-primary btn-sm mr-md-2" type="button" onclick="soldAnno(this.closest(\'.card\'))">' + soldButton + '</button>' +
        '<button class="btn btn-danger btn-sm mr-md-2" type="button" onclick="deleteAnno(this.closest(\'.card\'))">Удалить</button>' +
        '</div>' +
        '</div>' +
        '<div class="card-footer text-muted text-right">' + 'Автор объявления: ' + item.user.name + ', Почта: ' + item.user.email + '</div>';
    document.getElementById('box').appendChild(card);
}

function deleteAnno(card) {
    $.ajax({
        type: 'DELETE',
        url: 'http://localhost:8080/cars/announce' + '?annoId=' + card.getAttribute('id'),
    }).done(function (data) {
        card.hidden = true;
    }).fail(function (err) {
        alert(err.status);
    });
}

function soldAnno(card) {
    let id = card.getAttribute('id');
    $.ajax({
        type: 'PUT',
        url: 'http://localhost:8080/cars/announce' + '?annoId=' + id,
    }).done(function (data) {
        reloadItems();
    }).fail(function (err) {
        alert(err.status);
    });
}
