drip-java-project-2025
Описание: Консольное приложение для агрегации новостей из RSS—лент. Поддерживает параллельный парсинг, сохранение в базу данных (PostgreSQL), периодическое обновление и 
В консольном приложение есть:
1) Фильтрация по дате (за это отвевечает FilterByDateCommand)
1) Фильтрация по источнику (за это отвечает FilterBySourceCommand)
3) Фильтрация по категории (за это отввечает FilterByCategoryCommand)
4) Поиск по ключевому слову (за это отвечает SearchByKeywordCommand)
5) Аналитика: число статей по категориям" (за это отвечает AnalyticsByCategoryCommand)
6) Аналитика: число статей по источникам" (за это отвечает AnalyticsBySourceCommand)
7) Отображение последних новостей (за это отвечает LatestNewsCommand, в этом классе можно сделать сортировку по дате или по источнику и вывесити эту новость в консоль/ csv формат/ html формат/ json формат)
8) показать полный текст по ссылке (за это отвечает ShowMainTextCommand)
9) обновить список новостей вручную(за это отвечает NewsRefreshCommand)
10) Архивировать старые новости (старше 30 дней) (за это отвечает NewsRefreshCommand)

Использованные библиотеки:
Jsoup – для парсинга RSS/XML.
HikariCP – пул JDBC-соединений.
SLF4J + Logback – для логирования.
JUnit 5 – написание и выполнение юнит-тестов.
Mockito / mockito-inline – мокинг зависимостей в тестах.
H2 (in-memory) – интеграционные тесты с БД.

Чтоб запустить проект нужно 
1) git clone https://github.com/F5re/drip-java-project-2025.git
 cd drip-java-project-2025
3) настроить application.properties
4) mvn exec:java -Dexec.mainClass="newsagregator.console.ConsoleMenu"

Проект покрыт тестами (всего их 84), покрытие 76%.
