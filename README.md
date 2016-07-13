## Junior Java Developer Texuna Technologies test task
#### Запуск генератора:

 - Загрузить   [Generator-1.0.jar](https://github.com/svvorf/ReportGenerator/releases/download/1.0/Generator-1.0.jar)
 - `java -jar Generator-1.0.jar settings.xml source-data.tsv output.txt`


Дополнительный  функционал, не описанный в требованиях:
1. Для файла настроек происходит предварительная валидация с помощью XML Schema Definition.
2. Строки данных не разбиваются на границе страницы, а полностью переносятся на следующую (как в примере example-report.txt)
3. Разделители остаются на предыдущей строке, когда возможно. Пробел может быть совмещен с пробелом, отделяющим значение от | (как в примере example-report.txt)