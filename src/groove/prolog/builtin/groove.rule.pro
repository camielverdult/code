% Groove Prolog Interface
% Copyright (C) 2009 Michiel Hendriks, University of Twente
% 
% This library is free software; you can redistribute it and/or
% modify it under the terms of the GNU Lesser General Public
% License as published by the Free Software Foundation; either
% version 2.1 of the License, or (at your option) any later version.
% 
% This library is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
% Lesser General Public License for more details.
% 
% You should have received a copy of the GNU Lesser General Public
% License along with this library; if not, write to the Free Software
% Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

:-build_in(rule_name/1,'groove.prolog.builtin.rule.Predicate_rule_name').
:-build_in(rule/2,'groove.prolog.builtin.rule.Predicate_rule').
:-build_in(rule_enabled/1,'groove.prolog.builtin.rule.Predicate_rule_enabled').
:-build_in(rule_confluent/1,'groove.prolog.builtin.rule.Predicate_rule_confluent').

enabled_rule_name(RN) :- rule_name(RN), rule_enabled(RN).
confluent_rule_name(RN) :- rule_name(RN), rule_confluent(RN).
enabled_rule(R) :- enabled_rule_name(RN), rule(RN,R).
confluent_rule(R) :- confluent_rule_name(RN), rule(RN,R).