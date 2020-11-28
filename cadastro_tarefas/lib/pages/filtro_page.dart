import 'package:cadastrotarefas/model/tarefa.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

class FiltroPage extends StatefulWidget {
  static const ROUTE_NAME = '/filtro';
  static const CHAVE_CAMPO_ORDENACAO = 'campoOrdenacao';
  static const CHAVE_USAR_ORDEM_DECRESCENTE = 'usarOrdemDecrescente';
  static const CHAVE_FILTRO_DESCRICAO = 'filtroDescricao';

  @override
  _FiltroPageState createState() => _FiltroPageState();
}

class _FiltroPageState extends State<FiltroPage> {
  final _campos = {
    Tarefa.CAMPO_ID: 'Código', Tarefa.CAMPO_DESCRICAO: 'Descrição', Tarefa.CAMPO_PRAZO: 'Prazo',
  };
  SharedPreferences _prefs;
  final _filtroDescricaoController = TextEditingController();
  String _campoOrdenacao = Tarefa.CAMPO_ID;
  bool _usarOrdemDecrescente = false;
  bool _alterouValores = false;

  @override
  void initState() {
    super.initState();
    _lerPreferencias();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      child: Scaffold(
        appBar: AppBar(title: Text('Filtro e Ordenação')),
        body: _criarBody(),
      ),
      onWillPop: _onVoltarClick,
    );
  }

  Widget _criarBody() {
    return ListView(
      children: [
        Padding(
          padding: EdgeInsets.only(left: 10, top: 10),
          child: Text('Campo para ordenação'),
        ),
        for (final campo in _campos.keys)
          Row(
            children: [
              Radio(
                value: campo,
                groupValue: _campoOrdenacao,
                onChanged: _onCampoOrdenacaoChanged,
              ),
              Text(_campos[campo]),
            ],
          ),
        Row(
          children: [
            Checkbox(
              value: _usarOrdemDecrescente,
              onChanged: _onUsarOrdemDecrescenteChanged,
            ),
            Text('Usar ordem decrescente'),
          ],
        ),
        Padding(
          padding: EdgeInsets.symmetric(horizontal: 10),
          child: TextField(
            decoration: InputDecoration(
              labelText: 'Descrição começa com',
            ),
            controller: _filtroDescricaoController,
            onChanged: _onFiltroDescricaoChanged,
          ),
        ),
      ],
    );
  }

  _lerPreferencias() async {
    _prefs = await SharedPreferences.getInstance();
    setState(() {
      _campoOrdenacao = _prefs.getString(FiltroPage.CHAVE_CAMPO_ORDENACAO) ?? Tarefa.CAMPO_ID;
      _usarOrdemDecrescente = _prefs.getBool(FiltroPage.CHAVE_USAR_ORDEM_DECRESCENTE) == true;
      _filtroDescricaoController.text = _prefs.getString(FiltroPage.CHAVE_FILTRO_DESCRICAO) ?? '';
    });
  }

  Future<bool> _onVoltarClick() async {
    Navigator.pop(context, _alterouValores);
    return true;
  }

  _onCampoOrdenacaoChanged(String valor) {
    _prefs?.setString(FiltroPage.CHAVE_CAMPO_ORDENACAO, valor);
    _alterouValores = true;
    setState(() {
      _campoOrdenacao = valor;
    });
  }

  _onUsarOrdemDecrescenteChanged(bool checked) {
    _prefs?.setBool(FiltroPage.CHAVE_USAR_ORDEM_DECRESCENTE, checked);
    _alterouValores = true;
    setState(() {
      _usarOrdemDecrescente = checked;
    });
  }

  _onFiltroDescricaoChanged(String valor) {
    _prefs?.setString(FiltroPage.CHAVE_FILTRO_DESCRICAO, valor);
    _alterouValores = true;
  }

}
