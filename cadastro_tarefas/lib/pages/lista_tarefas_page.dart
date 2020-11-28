import 'package:cadastrotarefas/model/tarefa.dart';
import 'package:cadastrotarefas/pages/filtro_page.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

class ListaTarefasPage extends StatefulWidget {
  @override
  _ListaTarefasPageState createState() => _ListaTarefasPageState();
}

class _ListaTarefasPageState extends State<ListaTarefasPage> {
  static const ACAO_EDITAR = 'editar';
  static const ACAO_EXCLUIR = 'excluir';

  final _tarefas = [
    new Tarefa(id: 1, descricao: 'Fazer exercício 1', prazo: DateTime(2020, 10, 1))
  ];
  var _ultimoId = 1;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _criarAppBar(),
      body: _criarBody(),
      floatingActionButton: FloatingActionButton(
        onPressed: _abrirForm,
        tooltip: 'Nova Tarefa',
        child: Icon(Icons.add),
      ),
    );
  }

  Widget _criarAppBar() {
    return AppBar(
      title: Text('Tarefas'),
      actions: [
        IconButton(
          icon: Icon(Icons.filter_list),
          onPressed: _abrirPaginaFiltro,
        ),
      ],
    );
  }

  Widget _criarBody() {
    if (_tarefas.isEmpty) {
      return Center(
        child: Text('Nenhuma tarefa cadastrada',
          style: TextStyle(
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
      );
    }
    return ListView.separated(
      itemCount: _tarefas.length,
      itemBuilder: (_, int index) {
        final tarefa = _tarefas[index];
        return PopupMenuButton(
          child: ListTile(
            title: Text('${tarefa.id} - ${tarefa.descricao}'),
            subtitle: Text('${tarefa.prazoFormatado}'),
          ),
          itemBuilder: (_) => _criarItensMenuPopup(),
          onSelected: (String valorSelecionado) {
            if (valorSelecionado == ACAO_EDITAR) {
              _abrirForm(tarefaAtual: tarefa, indice: index);
            } else {
              _excluir(index);
            }
          },
        );
      },
      separatorBuilder: (_, __) => Divider(),
    );
  }

  List<PopupMenuEntry<String>> _criarItensMenuPopup() {
    return [
      PopupMenuItem(
        value: ACAO_EDITAR,
        child: Row(
          children: [
            Icon(Icons.edit, color: Colors.green),
            Padding(
              padding: EdgeInsets.only(left: 10),
              child: Text('Editar'),
            ),
          ],
        ),
      ),
      PopupMenuItem(
        value: ACAO_EXCLUIR,
        child: Row(
          children: [
            Icon(Icons.delete, color: Colors.red),
            Padding(
              padding: EdgeInsets.only(left: 10),
              child: Text('Excluir'),
            ),
          ],
        ),
      ),
    ];
  }

  void _abrirForm({Tarefa tarefaAtual, int indice}) {
    final key = GlobalKey<_ConteudoFormDialogState>();
    showDialog(
      context: context,
      builder: (_) => AlertDialog(
        title: Text(tarefaAtual == null
            ? 'Nova Tarefa' : 'Alterar Tarefa (${tarefaAtual.id})'),
        content: ConteudoFormDialog(key: key, tarefaAtual: tarefaAtual),
        actions: [
          FlatButton(
            child: Text('Cancelar'),
            onPressed: () => Navigator.pop(context),
          ),
          FlatButton(
            child: Text('Salvar'),
            onPressed: () {
              if (key.currentState.dadosValidos()) {
                setState(() {
                  final novaTarefa = key.currentState.novaTarefa;
                  if (novaTarefa.id == null) {
                    novaTarefa.id = ++_ultimoId;
                    _tarefas.add(novaTarefa);
                  } else {
                    _tarefas[indice] = novaTarefa;
                  }
                });
                Navigator.pop(context);
              }
            },
          ),
        ],
      ),
    );
  }

  void _excluir(int indice) {
    showDialog(
      context: context,
      builder: (_) => AlertDialog(
        title: Row(
          children: [
            Icon(Icons.warning, color: Colors.orange),
            Padding(
              padding: EdgeInsets.only(left: 10),
              child: Text('Atenção'),
            ),
          ],
        ),
        content: Text('Esse registro será removido definitivamente'),
        actions: [
          FlatButton(
            child: Text('Cancelar'),
            onPressed: () => Navigator.pop(context),
          ),
          FlatButton(
            child: Text('OK'),
            onPressed: () {
              setState(() {
                _tarefas.removeAt(indice);
              });
              Navigator.pop(context);
            },
          ),
        ],
      ),
    );
  }

  void _abrirPaginaFiltro() async {
    final alterouValores = await Navigator.of(context).pushNamed(FiltroPage.ROUTE_NAME);
    if (alterouValores == true) {
      // TODO
    }
  }

}

class ConteudoFormDialog extends StatefulWidget {
  final Tarefa tarefaAtual;

  ConteudoFormDialog({Key key, this.tarefaAtual}) : super(key: key);

  @override
  _ConteudoFormDialogState createState() => _ConteudoFormDialogState();
}

class _ConteudoFormDialogState extends State<ConteudoFormDialog> {

  final _formKey = GlobalKey<FormState>();
  final _descricaoController = TextEditingController();
  final _prazoController = TextEditingController();
  final _dateFormat = DateFormat('dd/MM/yyyy');

  @override
  void initState() {
    super.initState();
    _descricaoController.text = widget.tarefaAtual?.descricao ?? '';
    _prazoController.text = widget.tarefaAtual?.prazoFormatado ?? '';
  }

  @override
  Widget build(BuildContext context) {
    return Form(
      key: _formKey,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          TextFormField(
            controller: _descricaoController,
            decoration: InputDecoration(
              labelText: 'Descrição',
            ),
            validator: (String valor) {
              if (valor == null || valor.isEmpty) {
                return 'Informe a descrição';
              } else if (valor.length > 50) {
                return 'Informe no máximo 50 caracteres';
              }
              return null;
            },
          ),
          TextFormField(
            controller: _prazoController,
            decoration: InputDecoration(
              labelText: 'Prazo',
              prefixIcon: IconButton(
                icon: Icon(Icons.calendar_today),
                onPressed: _mostrarCalendario,
              ),
              suffixIcon: IconButton(
                icon: Icon(Icons.close),
                onPressed: () => _prazoController.clear(),
              ),
            ),
            readOnly: true,
          ),
        ],
      ),
    );
  }

  _mostrarCalendario() async {
    final dataFormatada = _prazoController.text;
    var data = DateTime.now();
    if (dataFormatada.isNotEmpty) {
      data = _dateFormat.parse(dataFormatada);
    }
    final agora = DateTime.now();
    final dataSelecionada = await showDatePicker(
      context: context,
      initialDate: data,
      firstDate: agora.subtract(Duration(days: 365 * 5)),
      lastDate: agora.add(Duration(days: 365 * 5)),
    );
    if (dataSelecionada != null) {
      setState(() {
        _prazoController.text = _dateFormat.format(dataSelecionada);
      });
    }
  }

  bool dadosValidos() => _formKey.currentState.validate();

  Tarefa get novaTarefa => Tarefa(
    id: widget.tarefaAtual?.id,
    descricao: _descricaoController.text,
    prazo: _prazoController.text.isEmpty
        ? null : _dateFormat.parse(_prazoController.text),
  );

}
