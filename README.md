# 🚀 Sprint 7 — Автоматизированные тесты API Яндекс.Самокат

Этот репозиторий содержит автотесты REST API для сервиса Яндекс.Самокат.  
Тесты написаны с использованием **Java 11**, **JUnit4**, **RestAssured** и **Allure**.

---

## 📦 Состав проекта

- `src/test/java/scooter/test/` — тесты для каждого endpoint:
    - `CreateCourierTests`
    - `LoginCourierTests`
    - `CourierDeleteTests`
    - `OrderTests`
    - `OrderListTests`
    - `OrderTrackTests`
    - `OrderAcceptTests` (с параметризацией)

- `src/test/java/scooter/client/` — REST-клиенты:
    - `CourierClient`
    - `OrderClient`

- `target/allure-results/` — результат выполнения тестов (Allure JSON)

---

## 🛠️ Технологии

| Технология      | Версия      |
|-----------------|-------------|
| Java            | 11          |
| Maven           | 3.x         |
| JUnit           | 4.13.2      |
| RestAssured     | 5.3.0       |
| Allure          | 2.13+       |

---

## ✅ Что покрыто

- Создание, логин и удаление курьера
- Создание заказов с параметрами
- Получение списка заказов с фильтрами, пагинацией
- Получение заказа по треку
- Принятие заказа курьером
- Проверки:
    - Позитивные сценарии
    - Негативные сценарии (400, 404, 409)
    - Параметризованные тесты (некорректные ID)

---

## ▶️ Запуск тестов

### Maven (сгенерировать Allure-отчёт)
```bash
mvn clean test
mvn allure:serve
