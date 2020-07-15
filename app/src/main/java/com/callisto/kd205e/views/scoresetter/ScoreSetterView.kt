package com.callisto.kd205e.views.scoresetter

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.callisto.kd205e.R
import com.callisto.kd205e.views.base.CustomLinearLayout
import kotlinx.android.synthetic.main.view_score_setter.view.*
import timber.log.Timber

class ScoreSetterView :
    CustomLinearLayout<ScoreSetterState, ScoreSetterModel>
{
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    {
        initialize(attrs, context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    {
        initialize(attrs, context)
    }

    private var iconId: Int = 0
    private var bonus: Int = 0
    private var baseScore: Int = 0
    private lateinit var attributeName: String

    override lateinit var viewModel: ScoreSetterModel

    private fun initialize(attrs: AttributeSet, context: Context)
    {
        View.inflate(context, R.layout.view_score_setter, this)

        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ScoreSetterView,
            0, 0
        )

        try
        {
            attributeName = typedArray.getString(R.styleable.ScoreSetterView_attribute_name)!!
            baseScore = typedArray.getInteger(R.styleable.ScoreSetterView_attribute_base_score, 0)
            bonus = typedArray.getInteger(R.styleable.ScoreSetterView_attribute_bonus, 0)
//            iconId = typedArray.getInteger(R.styleable.ScoreSetterView_attribute_icon_id, 0)

            txtBaseScore.text = baseScore.toString()
            txtBonus.text = bonus.toString()
            txtFinalScore.text = (baseScore + bonus).toString()
//            imgAttributeIcon.setImageDrawable(resources.getDrawable(iconId, null))

            viewModel = ScoreSetterModel()

            viewModel.attributeModifier.value = bonus
            viewModel.attributeScore.value = baseScore
            viewModel.attributeName.value = attributeName
        }
        catch (e: Exception)
        {
            Timber.e(e)
        }
        finally
        {
            typedArray.recycle()
        }
    }

    override fun onLifecycleOwnerAttached(lifecycleOwner: LifecycleOwner)
    {
        observeLiveData(lifecycleOwner)
    }

    private fun observeLiveData(lifecycleOwner: LifecycleOwner)
    {
        viewModel.getLabel().observe(lifecycleOwner, Observer {

        })

        viewModel.getScore().observe(lifecycleOwner, Observer {
            txtFinalScore.text = viewModel.getFinalScore().toString()
        })

        viewModel.getModifier().observe(lifecycleOwner, Observer {
            txtFinalScore.text = viewModel.getFinalScore().toString()
        })
    }
}