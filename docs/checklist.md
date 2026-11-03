# Проверка Assignment 6, вариант 12

Проверено 3 ноября 2026 года.

| Требование | Подтверждение |
|---|---|
| Java 17, JUnit 5, без других библиотек | compiler release17; единственная зависимость pom — JUnit5 test scope |
| >=2 источника | MeterReadingSource, GridStatusSource |
| >=5 подписчиков | GridDashboard, OutageAlarm, UsageAggregator, InspectionService, EventLog; дополнительно OneShotNotice |
| Generic Subject/Observer, immutable typed events | Subject<E>, Observer<E>, EnergyEvent record, EventType |
| Подписка по типу, join/leave из handler | EnumMap+CopyOnWriteArraySet; тесты self-unsubscribe и subscribe внутри callback |
| Observer также Subject | UsageAggregator публикует AGGREGATE после атомарного обновления summary |
| Выбор push/pull объяснён | Push immutable snapshot; README |
| Composite >=3 уровня, >=2 контейнера, >=4 листа | Grid→Substation→Line→devices: 4 уровня, 3 контейнера, 4 базовых листа + Transformer |
| Generic Visitor<R>, double dispatch | Конкретный accept вызывает типизированный visit(this) |
| Numeric/stateful/filter/export visitors | NetLoad, Inventory, HighLoad, JsonExport; дополнительно LeafCount для trade-off |
| Корректная вложенность JSON | Точный JUnit expected JSON + независимый Python json.loads реального CLI |
| Элементы только данные/accept | Immutable records; рекурсия и операции в visitors |
| Observer запускает visitor только поддерева | InspectionService получает только event.subtree; line-1=4.1 в fixture против Grid=22.3 |
| Результат возвращается событием | INSPECTION_REPORT → EventLog |
| >=3 producer threads, configurable ExecutorService | Main: 3 задачи, pool аргумент>=3; проверены 3 и default4 |
| Shared state thread-safe, нет глобального subject lock | COW snapshots, AtomicReference<UsageSummary>, AtomicInteger, ConcurrentLinkedQueue |
| Graceful normal shutdown | Все Future ожидаются; shutdown/awaitTermination; тест прекращения после producer failure |
| Constructor injection, composition root, без lazy компонентов | Main; InspectionService/OneShotNotice/ProducerRunner получают зависимости конструкторами |
| Два trade-off коммита | 7a996a6 — новый visitor без правок элементов; 3524deb — новый элемент с правками всех visitors |
| >=15 JUnit tests | 28 tests, 0 failures/errors/skipped; mvn clean verify BUILD SUCCESS |
| Stress 4×1000 и ручные visitor values | Ровно4000 доставок/агрегатов/записей, сумма4000kWh; fixture22.3kW/line4.1kW |
| README, report, UML | README.md, REPORT.md, docs/uml.svg/png/dot; диаграмма отрисована и просмотрена |

## Проверка реального процесса

JAR запущен с default pool4 и pool3. В обоих случаях: 20 показаний, 30kWh, 10 аварий,
20 агрегатов+10 отчётов, 1 one-shot; финальное дерево22.8kW/9устройств, инспекцияline-1=4.6kW.
JSON обоих запусков разобран независимым JSON-парсером. Оба процесса завершились без зависания.
Выполнен независимый read-only аудит: существенных дефектов и лишней сложности не обнаружено.

## Не закрыто автоматически

* GitHub link: нужен отдельный репозиторий и публикация всей истории.
* Meaningful history over 2 weeks: все коммиты созданы за одну сессию, поэтому этот пункт не подтверждён.
* Защита/live coding: выполняет студент.
