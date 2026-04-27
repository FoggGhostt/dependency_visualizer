# depvis — Dependency Visualizer

CLI-инструмент для анализа зависимостей Java-проекта. Сканирует `.java`-файлы,
извлекает зависимости между классами и пакетами, строит граф и генерирует
JSON / HTML-отчёты, ищет циклы и проверяет архитектурные правила.

Пригодится для онбординга, рефакторинга и базового контроля архитектуры.

## Требования

- Java 25 (для сборки и запуска)
- Maven 3.9+

## Сборка

```sh
mvn package
```

Готовый исполняемый fat-jar появится по пути `target/depvis-1.0.0-SNAPSHOT.jar`.

## Команды

### 1. analyze — просканировать проект и собрать `graph.json`

```sh
java -jar target/depvis-1.0.0-SNAPSHOT.jar analyze ./demo --out out
```

Опции:

- `<projectPath>` — путь к Java-проекту (обязательный)
- `--out <dir>` — каталог для результата (по умолчанию `out`)
- `--include <glob>` — include-паттерн (можно несколько)
- `--exclude <glob>` — exclude-паттерн (можно несколько)

### 2. cycles — вывести найденные циклы зависимостей

```sh
java -jar target/depvis-1.0.0-SNAPSHOT.jar cycles out/graph.json
```

### 3. report — сгенерировать простой HTML-отчёт

```sh
java -jar target/depvis-1.0.0-SNAPSHOT.jar report out/graph.json --out out/report.html
```

### 4. check — проверить запрещённые архитектурные правила

```sh
java -jar target/depvis-1.0.0-SNAPSHOT.jar check out/graph.json --config config.json
```

Если найдено хотя бы одно нарушение — exit code = `1`.

Конфиг можно сохранять как `.json` или `.yaml`/`.yml` — формат определяется
автоматически по расширению.

## Демо

В папке `demo/` лежит небольшой Java-проект, где специально подсажены:

- цикл на уровне классов между `core.User` и `core.Order`
- ребро `api → persistence`, нарушающее правило из `config.json`

End-to-end запуск:

```sh
mvn package
java -jar target/depvis-1.0.0-SNAPSHOT.jar analyze ./demo --out out
java -jar target/depvis-1.0.0-SNAPSHOT.jar cycles out/graph.json
java -jar target/depvis-1.0.0-SNAPSHOT.jar report out/graph.json --out out/report.html
java -jar target/depvis-1.0.0-SNAPSHOT.jar check out/graph.json --config config.json
```

## Тесты

```sh
mvn test
```

## Структура проекта

```
src/main/java/com/example/depvis/
  cli/        picocli-команды (analyze, cycles, report, check)
  config/     загрузка конфига (auto-detect JSON / YAML)
  scan/       рекурсивный поиск .java-файлов с include/exclude
  parser/     извлечение зависимостей через JavaParser
  model/      Node, Edge, GraphSnapshot, AnalysisResult, GraphBuilder
  analysis/   MetricsCalculator, CycleDetector, RuleChecker
  export/     JsonExporter, HtmlReporter
  util/       PathUtils
demo/         маленький Java-проект для ручных прогонов
config.json   пример конфига с forbidden-правилами
```

## Известные ограничения и TODO

Реализация намеренно упрощена (учебный проект). К доработке:

- generics в парсере (сейчас учитываются только raw-типы)
- method calls и наследование (`extends`/`implements`)
- wildcard imports
- layered architecture rules (поле `layers` в конфиге)
- визуализация графа в HTML-отчёте (Graphviz / D3)
