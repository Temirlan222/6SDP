# Assignment 6 — Observer and Visitor. Variant 12: Energy Grid

## Задача

Небольшая Java 17 система обрабатывает показания счётчиков и аварии энергосети.
Данные синтетические; runtime использует только стандартную библиотеку, тесты — JUnit 5.

## Observer

Два источника публикуют immutable typed EnergyEvent. Подписки группируются по EventType собственными
generic Subject/Observer. Шесть слушателей выполняют роли dashboard, alarm, aggregator, inspection, log и one-shot notice.
Push передаёт согласованное событие без повторного чтения источника. Copy-on-write snapshots позволяют менять подписки
из обработчиков; уже начатая публикация может завершить доставку из старого snapshot.
UsageAggregator одновременно Observer и Subject: атомарно обновляет summary и публикует AGGREGATE.

## Visitor

Composite: Grid → Substation → Line → Consumer/Generator/Battery/SmartMeter/Transformer.
Generic Visitor<R> использует double dispatch через accept и типизированные visit-overloads.
NetLoadVisitor считает kW; InventoryVisitor накапливает количества; HighLoadVisitor возвращает список;
JsonExportVisitor формирует корректно вложенный JSON. LeafCountVisitor добавлен отдельным коммитом.
Обход находится в посетителях, элементы содержат данные и accept без бизнес-расчётов.

## Интеграция и concurrency

Авария запускает visitor только на связанной линии, затем создаётся INSPECTION_REPORT для EventLog.
Main собирает зависимости конструкторами. Три producer-задачи работают через настраиваемый ExecutorService.
AtomicReference хранит согласованный агрегат; AtomicInteger и ConcurrentLinkedQueue защищают shared state.
Future.get и shutdown/awaitTermination обеспечивают нормальное завершение всех принятых задач и вложенных уведомлений.

## Проверка и trade-offs

28 JUnit tests; стресс 4×1000 требует ровно 4000 входных событий и агрегатов.
Ручные значения: базовая сеть 22.3 kW, отдельная линия 4.1 kW; финальный Transformer добавляет 0.5 kW.
Коммит 7a996a6 добавляет visitor без правок элементов; 3524deb добавляет элемент с обновлением всего visitor-контракта.
Подробные списки: docs/trade-offs.md. Диаграмма: docs/uml.svg.

## Ограничения сдачи

Нужно опубликовать отдельный GitHub-репозиторий с полной историей и указать его ссылку при сдаче.
Все локальные коммиты сделаны 3 ноября 2026: требование истории разработки за две недели этим не подтверждается.
Защита и live coding остаются обязательными.
