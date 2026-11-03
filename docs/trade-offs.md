# Trade-off proof — реальные коммиты

## 1. Новая операция: 7a996a6

Добавлен LeafCountVisitor. Изменённые файлы:

* `README.md`
* `src/main/java/edu/aitu/energy/visitor/LeafCountVisitor.java`
* `src/test/java/edu/aitu/energy/LeafCountVisitorTest.java`

Классы элементов и Visitor.java не изменялись. Все 27 тестов на этом коммите проходят.
Новая операция реализует существующие overload visit; каждому типу элемента соответствует поведение нового посетителя.

## 2. Новый элемент: 3524deb

Добавлен Transformer. Изменённые файлы:

* `README.md`
* `src/main/java/edu/aitu/energy/Main.java`
* `src/main/java/edu/aitu/energy/tree/Transformer.java`
* `src/main/java/edu/aitu/energy/visitor/HighLoadVisitor.java`
* `src/main/java/edu/aitu/energy/visitor/InventoryVisitor.java`
* `src/main/java/edu/aitu/energy/visitor/JsonExportVisitor.java`
* `src/main/java/edu/aitu/energy/visitor/LeafCountVisitor.java`
* `src/main/java/edu/aitu/energy/visitor/NetLoadVisitor.java`
* `src/main/java/edu/aitu/energy/visitor/Visitor.java`
* `src/test/java/edu/aitu/energy/TransformerTest.java`

Нужен новый overload Visitor.visit(Transformer), реализация этого overload во всех 5 посетителях,
accept в новом элементе, тест и подключение в пример Main. Все 28 тестов проходят.

Вывод: Visitor упрощает новые операции, но удорожает добавление типов элементов.
Если типы узлов постоянно меняются, стоимость обновления всех посетителей может сделать паттерн неудачным выбором.

Проверить локально:

```shell
git show --stat 7a996a6
git show --stat 3524deb
```

Коммиты созданы настоящими этапами в один день; они не доказывают двухнедельный процесс разработки.
