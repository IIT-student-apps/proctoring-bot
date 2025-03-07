package org.bsuir.proctoringbot.bot.statemachine;

import java.util.*;

public class StateMachine {

    private final Map<State, List<State>> transitions = new HashMap<>();
    private StateBuilder currentBuilder;

    public static StateMachine create() {
        return new StateMachine();
    }

    public StateBuilder registerState(State state) {
        this.currentBuilder = new StateBuilder(state);
        transitions.putIfAbsent(state, new ArrayList<>());
        return currentBuilder;
    }

    public boolean checkState(State previousState, State nextState) {
        List<State> allNextStates = transitions.get(previousState);
        return allNextStates.contains(nextState);
    }

    public class StateBuilder {
        private final State fromState;

        private StateBuilder(State fromState) {
            this.fromState = fromState;
        }

        public StateBuilder add(State toState) {
            transitions.get(fromState).add(toState);
            return this;
        }

        public StateMachine and() {
            return StateMachine.this;
        }

        public StateMachine build() {
            return StateMachine.this;
        }
    }

    public void printTransitions() {
        transitions.forEach((key, value) ->
                System.out.println(key + " -> " + value)
        );
    }
}
