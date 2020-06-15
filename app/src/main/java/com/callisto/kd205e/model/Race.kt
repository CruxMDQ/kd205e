package com.callisto.kd205e.model

class Race
(
    _name: String,
    _adjective: String,
    _abilityScoreModifiers: Set<Attribute>
)
{
    var name: String = _name
    var adjective: String = _adjective
    var abilityScoreModifiers: Set<Attribute> = _abilityScoreModifiers

    lateinit var traits: Set<Trait>

    constructor
    (
        _name: String,
        _adjective: String,
        _abilityScoreModifiers: Set<Attribute>,
        _traits: Set<Trait>
    ) : this(_name, _adjective, _abilityScoreModifiers)
    {
        traits = _traits
    }

    override fun toString(): String
    {
        return name
    }
}