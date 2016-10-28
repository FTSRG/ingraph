/**
 * generated by Xtext 2.10.0
 */
package org.slizaa.neo4j.opencypher.openCypher.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.slizaa.neo4j.opencypher.openCypher.Expression;
import org.slizaa.neo4j.opencypher.openCypher.Expression3Part;
import org.slizaa.neo4j.opencypher.openCypher.MapLiteral;
import org.slizaa.neo4j.opencypher.openCypher.MapLiteralEntry;
import org.slizaa.neo4j.opencypher.openCypher.NodeLabel;
import org.slizaa.neo4j.opencypher.openCypher.NodeLabels;
import org.slizaa.neo4j.opencypher.openCypher.OpenCypherPackage;
import org.slizaa.neo4j.opencypher.openCypher.PropertyExpression;
import org.slizaa.neo4j.opencypher.openCypher.PropertyLookup;
import org.slizaa.neo4j.opencypher.openCypher.RemoveItem;
import org.slizaa.neo4j.opencypher.openCypher.Variable;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Map Literal</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.slizaa.neo4j.opencypher.openCypher.impl.MapLiteralImpl#getVariable <em>Variable</em>}</li>
 *   <li>{@link org.slizaa.neo4j.opencypher.openCypher.impl.MapLiteralImpl#getNodeLabels <em>Node Labels</em>}</li>
 *   <li>{@link org.slizaa.neo4j.opencypher.openCypher.impl.MapLiteralImpl#getOperator <em>Operator</em>}</li>
 *   <li>{@link org.slizaa.neo4j.opencypher.openCypher.impl.MapLiteralImpl#getLeft <em>Left</em>}</li>
 *   <li>{@link org.slizaa.neo4j.opencypher.openCypher.impl.MapLiteralImpl#getExpression3Parts <em>Expression3 Parts</em>}</li>
 *   <li>{@link org.slizaa.neo4j.opencypher.openCypher.impl.MapLiteralImpl#getNodeLabelList <em>Node Label List</em>}</li>
 *   <li>{@link org.slizaa.neo4j.opencypher.openCypher.impl.MapLiteralImpl#getPropertyLookup <em>Property Lookup</em>}</li>
 *   <li>{@link org.slizaa.neo4j.opencypher.openCypher.impl.MapLiteralImpl#getEntries <em>Entries</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MapLiteralImpl extends PropertiesImpl implements MapLiteral
{
  /**
   * The cached value of the '{@link #getVariable() <em>Variable</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getVariable()
   * @generated
   * @ordered
   */
  protected Variable variable;

  /**
   * The cached value of the '{@link #getNodeLabels() <em>Node Labels</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNodeLabels()
   * @generated
   * @ordered
   */
  protected NodeLabels nodeLabels;

  /**
   * The default value of the '{@link #getOperator() <em>Operator</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOperator()
   * @generated
   * @ordered
   */
  protected static final String OPERATOR_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getOperator() <em>Operator</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOperator()
   * @generated
   * @ordered
   */
  protected String operator = OPERATOR_EDEFAULT;

  /**
   * The cached value of the '{@link #getLeft() <em>Left</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLeft()
   * @generated
   * @ordered
   */
  protected Expression left;

  /**
   * The cached value of the '{@link #getExpression3Parts() <em>Expression3 Parts</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getExpression3Parts()
   * @generated
   * @ordered
   */
  protected EList<Expression3Part> expression3Parts;

  /**
   * The cached value of the '{@link #getNodeLabelList() <em>Node Label List</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNodeLabelList()
   * @generated
   * @ordered
   */
  protected EList<NodeLabel> nodeLabelList;

  /**
   * The cached value of the '{@link #getPropertyLookup() <em>Property Lookup</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPropertyLookup()
   * @generated
   * @ordered
   */
  protected EList<PropertyLookup> propertyLookup;

  /**
   * The cached value of the '{@link #getEntries() <em>Entries</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEntries()
   * @generated
   * @ordered
   */
  protected EList<MapLiteralEntry> entries;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MapLiteralImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return OpenCypherPackage.Literals.MAP_LITERAL;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Variable getVariable()
  {
    return variable;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetVariable(Variable newVariable, NotificationChain msgs)
  {
    Variable oldVariable = variable;
    variable = newVariable;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OpenCypherPackage.MAP_LITERAL__VARIABLE, oldVariable, newVariable);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setVariable(Variable newVariable)
  {
    if (newVariable != variable)
    {
      NotificationChain msgs = null;
      if (variable != null)
        msgs = ((InternalEObject)variable).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OpenCypherPackage.MAP_LITERAL__VARIABLE, null, msgs);
      if (newVariable != null)
        msgs = ((InternalEObject)newVariable).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OpenCypherPackage.MAP_LITERAL__VARIABLE, null, msgs);
      msgs = basicSetVariable(newVariable, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OpenCypherPackage.MAP_LITERAL__VARIABLE, newVariable, newVariable));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NodeLabels getNodeLabels()
  {
    return nodeLabels;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetNodeLabels(NodeLabels newNodeLabels, NotificationChain msgs)
  {
    NodeLabels oldNodeLabels = nodeLabels;
    nodeLabels = newNodeLabels;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OpenCypherPackage.MAP_LITERAL__NODE_LABELS, oldNodeLabels, newNodeLabels);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setNodeLabels(NodeLabels newNodeLabels)
  {
    if (newNodeLabels != nodeLabels)
    {
      NotificationChain msgs = null;
      if (nodeLabels != null)
        msgs = ((InternalEObject)nodeLabels).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OpenCypherPackage.MAP_LITERAL__NODE_LABELS, null, msgs);
      if (newNodeLabels != null)
        msgs = ((InternalEObject)newNodeLabels).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OpenCypherPackage.MAP_LITERAL__NODE_LABELS, null, msgs);
      msgs = basicSetNodeLabels(newNodeLabels, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OpenCypherPackage.MAP_LITERAL__NODE_LABELS, newNodeLabels, newNodeLabels));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getOperator()
  {
    return operator;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setOperator(String newOperator)
  {
    String oldOperator = operator;
    operator = newOperator;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OpenCypherPackage.MAP_LITERAL__OPERATOR, oldOperator, operator));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Expression getLeft()
  {
    return left;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetLeft(Expression newLeft, NotificationChain msgs)
  {
    Expression oldLeft = left;
    left = newLeft;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, OpenCypherPackage.MAP_LITERAL__LEFT, oldLeft, newLeft);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setLeft(Expression newLeft)
  {
    if (newLeft != left)
    {
      NotificationChain msgs = null;
      if (left != null)
        msgs = ((InternalEObject)left).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - OpenCypherPackage.MAP_LITERAL__LEFT, null, msgs);
      if (newLeft != null)
        msgs = ((InternalEObject)newLeft).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - OpenCypherPackage.MAP_LITERAL__LEFT, null, msgs);
      msgs = basicSetLeft(newLeft, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OpenCypherPackage.MAP_LITERAL__LEFT, newLeft, newLeft));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Expression3Part> getExpression3Parts()
  {
    if (expression3Parts == null)
    {
      expression3Parts = new EObjectContainmentEList<Expression3Part>(Expression3Part.class, this, OpenCypherPackage.MAP_LITERAL__EXPRESSION3_PARTS);
    }
    return expression3Parts;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<NodeLabel> getNodeLabelList()
  {
    if (nodeLabelList == null)
    {
      nodeLabelList = new EObjectContainmentEList<NodeLabel>(NodeLabel.class, this, OpenCypherPackage.MAP_LITERAL__NODE_LABEL_LIST);
    }
    return nodeLabelList;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<PropertyLookup> getPropertyLookup()
  {
    if (propertyLookup == null)
    {
      propertyLookup = new EObjectContainmentEList<PropertyLookup>(PropertyLookup.class, this, OpenCypherPackage.MAP_LITERAL__PROPERTY_LOOKUP);
    }
    return propertyLookup;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<MapLiteralEntry> getEntries()
  {
    if (entries == null)
    {
      entries = new EObjectContainmentEList<MapLiteralEntry>(MapLiteralEntry.class, this, OpenCypherPackage.MAP_LITERAL__ENTRIES);
    }
    return entries;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case OpenCypherPackage.MAP_LITERAL__VARIABLE:
        return basicSetVariable(null, msgs);
      case OpenCypherPackage.MAP_LITERAL__NODE_LABELS:
        return basicSetNodeLabels(null, msgs);
      case OpenCypherPackage.MAP_LITERAL__LEFT:
        return basicSetLeft(null, msgs);
      case OpenCypherPackage.MAP_LITERAL__EXPRESSION3_PARTS:
        return ((InternalEList<?>)getExpression3Parts()).basicRemove(otherEnd, msgs);
      case OpenCypherPackage.MAP_LITERAL__NODE_LABEL_LIST:
        return ((InternalEList<?>)getNodeLabelList()).basicRemove(otherEnd, msgs);
      case OpenCypherPackage.MAP_LITERAL__PROPERTY_LOOKUP:
        return ((InternalEList<?>)getPropertyLookup()).basicRemove(otherEnd, msgs);
      case OpenCypherPackage.MAP_LITERAL__ENTRIES:
        return ((InternalEList<?>)getEntries()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case OpenCypherPackage.MAP_LITERAL__VARIABLE:
        return getVariable();
      case OpenCypherPackage.MAP_LITERAL__NODE_LABELS:
        return getNodeLabels();
      case OpenCypherPackage.MAP_LITERAL__OPERATOR:
        return getOperator();
      case OpenCypherPackage.MAP_LITERAL__LEFT:
        return getLeft();
      case OpenCypherPackage.MAP_LITERAL__EXPRESSION3_PARTS:
        return getExpression3Parts();
      case OpenCypherPackage.MAP_LITERAL__NODE_LABEL_LIST:
        return getNodeLabelList();
      case OpenCypherPackage.MAP_LITERAL__PROPERTY_LOOKUP:
        return getPropertyLookup();
      case OpenCypherPackage.MAP_LITERAL__ENTRIES:
        return getEntries();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case OpenCypherPackage.MAP_LITERAL__VARIABLE:
        setVariable((Variable)newValue);
        return;
      case OpenCypherPackage.MAP_LITERAL__NODE_LABELS:
        setNodeLabels((NodeLabels)newValue);
        return;
      case OpenCypherPackage.MAP_LITERAL__OPERATOR:
        setOperator((String)newValue);
        return;
      case OpenCypherPackage.MAP_LITERAL__LEFT:
        setLeft((Expression)newValue);
        return;
      case OpenCypherPackage.MAP_LITERAL__EXPRESSION3_PARTS:
        getExpression3Parts().clear();
        getExpression3Parts().addAll((Collection<? extends Expression3Part>)newValue);
        return;
      case OpenCypherPackage.MAP_LITERAL__NODE_LABEL_LIST:
        getNodeLabelList().clear();
        getNodeLabelList().addAll((Collection<? extends NodeLabel>)newValue);
        return;
      case OpenCypherPackage.MAP_LITERAL__PROPERTY_LOOKUP:
        getPropertyLookup().clear();
        getPropertyLookup().addAll((Collection<? extends PropertyLookup>)newValue);
        return;
      case OpenCypherPackage.MAP_LITERAL__ENTRIES:
        getEntries().clear();
        getEntries().addAll((Collection<? extends MapLiteralEntry>)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case OpenCypherPackage.MAP_LITERAL__VARIABLE:
        setVariable((Variable)null);
        return;
      case OpenCypherPackage.MAP_LITERAL__NODE_LABELS:
        setNodeLabels((NodeLabels)null);
        return;
      case OpenCypherPackage.MAP_LITERAL__OPERATOR:
        setOperator(OPERATOR_EDEFAULT);
        return;
      case OpenCypherPackage.MAP_LITERAL__LEFT:
        setLeft((Expression)null);
        return;
      case OpenCypherPackage.MAP_LITERAL__EXPRESSION3_PARTS:
        getExpression3Parts().clear();
        return;
      case OpenCypherPackage.MAP_LITERAL__NODE_LABEL_LIST:
        getNodeLabelList().clear();
        return;
      case OpenCypherPackage.MAP_LITERAL__PROPERTY_LOOKUP:
        getPropertyLookup().clear();
        return;
      case OpenCypherPackage.MAP_LITERAL__ENTRIES:
        getEntries().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case OpenCypherPackage.MAP_LITERAL__VARIABLE:
        return variable != null;
      case OpenCypherPackage.MAP_LITERAL__NODE_LABELS:
        return nodeLabels != null;
      case OpenCypherPackage.MAP_LITERAL__OPERATOR:
        return OPERATOR_EDEFAULT == null ? operator != null : !OPERATOR_EDEFAULT.equals(operator);
      case OpenCypherPackage.MAP_LITERAL__LEFT:
        return left != null;
      case OpenCypherPackage.MAP_LITERAL__EXPRESSION3_PARTS:
        return expression3Parts != null && !expression3Parts.isEmpty();
      case OpenCypherPackage.MAP_LITERAL__NODE_LABEL_LIST:
        return nodeLabelList != null && !nodeLabelList.isEmpty();
      case OpenCypherPackage.MAP_LITERAL__PROPERTY_LOOKUP:
        return propertyLookup != null && !propertyLookup.isEmpty();
      case OpenCypherPackage.MAP_LITERAL__ENTRIES:
        return entries != null && !entries.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
  {
    if (baseClass == RemoveItem.class)
    {
      switch (derivedFeatureID)
      {
        case OpenCypherPackage.MAP_LITERAL__VARIABLE: return OpenCypherPackage.REMOVE_ITEM__VARIABLE;
        case OpenCypherPackage.MAP_LITERAL__NODE_LABELS: return OpenCypherPackage.REMOVE_ITEM__NODE_LABELS;
        default: return -1;
      }
    }
    if (baseClass == PropertyExpression.class)
    {
      switch (derivedFeatureID)
      {
        default: return -1;
      }
    }
    if (baseClass == Expression.class)
    {
      switch (derivedFeatureID)
      {
        case OpenCypherPackage.MAP_LITERAL__OPERATOR: return OpenCypherPackage.EXPRESSION__OPERATOR;
        case OpenCypherPackage.MAP_LITERAL__LEFT: return OpenCypherPackage.EXPRESSION__LEFT;
        case OpenCypherPackage.MAP_LITERAL__EXPRESSION3_PARTS: return OpenCypherPackage.EXPRESSION__EXPRESSION3_PARTS;
        case OpenCypherPackage.MAP_LITERAL__NODE_LABEL_LIST: return OpenCypherPackage.EXPRESSION__NODE_LABEL_LIST;
        case OpenCypherPackage.MAP_LITERAL__PROPERTY_LOOKUP: return OpenCypherPackage.EXPRESSION__PROPERTY_LOOKUP;
        default: return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
  {
    if (baseClass == RemoveItem.class)
    {
      switch (baseFeatureID)
      {
        case OpenCypherPackage.REMOVE_ITEM__VARIABLE: return OpenCypherPackage.MAP_LITERAL__VARIABLE;
        case OpenCypherPackage.REMOVE_ITEM__NODE_LABELS: return OpenCypherPackage.MAP_LITERAL__NODE_LABELS;
        default: return -1;
      }
    }
    if (baseClass == PropertyExpression.class)
    {
      switch (baseFeatureID)
      {
        default: return -1;
      }
    }
    if (baseClass == Expression.class)
    {
      switch (baseFeatureID)
      {
        case OpenCypherPackage.EXPRESSION__OPERATOR: return OpenCypherPackage.MAP_LITERAL__OPERATOR;
        case OpenCypherPackage.EXPRESSION__LEFT: return OpenCypherPackage.MAP_LITERAL__LEFT;
        case OpenCypherPackage.EXPRESSION__EXPRESSION3_PARTS: return OpenCypherPackage.MAP_LITERAL__EXPRESSION3_PARTS;
        case OpenCypherPackage.EXPRESSION__NODE_LABEL_LIST: return OpenCypherPackage.MAP_LITERAL__NODE_LABEL_LIST;
        case OpenCypherPackage.EXPRESSION__PROPERTY_LOOKUP: return OpenCypherPackage.MAP_LITERAL__PROPERTY_LOOKUP;
        default: return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (operator: ");
    result.append(operator);
    result.append(')');
    return result.toString();
  }

} //MapLiteralImpl